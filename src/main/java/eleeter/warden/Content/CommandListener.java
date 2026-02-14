package eleeter.warden.Content;

import eleeter.warden.bot.PenaltyManager;
import eleeter.warden.bot.ServiceManager;
import eleeter.warden.utils.config.Config;
import eleeter.warden.utils.config.BotMessages;
import eleeter.warden.utils.config.Colors;
import eleeter.warden.utils.config.EmbedConfig;
import eleeter.warden.utils.SanitizationEngine;
import eleeter.warden.ui.ControlPanel;
import eleeter.warden.ui.renderer.ArchiveRenderer;
import eleeter.warden.ui.renderer.CaseStatusRenderer;
import eleeter.warden.ui.renderer.ReportRenderer;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandListener extends ListenerAdapter
{
    private final PenaltyManager pm;
    private final java.util.Map<Long, java.util.List<Long>> rawReportLog = new ConcurrentHashMap<>();
    private final java.util.Map<Long, java.util.Set<Long>> reportHeatMap = new ConcurrentHashMap<>();

    public CommandListener(PenaltyManager pm)
    {
        this.pm = pm;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {


        if (event.getComponentId().equals("warden:close_case"))
        {
            event.reply("Case acknowledged. System purging channel...").setEphemeral(true).queue();
            event.getChannel().delete().queueAfter(3, java.util.concurrent.TimeUnit.SECONDS);
            return;
        }
        if (!event.getComponentId().startsWith(Config.ONBUTTONINTERACTION))
        {
            event.deferEdit().queue();
        }

        if (event.getComponentId().startsWith(Config.ONBUTTONINTERACTION))
        {
            String service = event.getComponentId().split(EmbedConfig.RANDOM_SYMBOL_4)[1];
            ServiceManager.toggle(service);
            event.editMessageEmbeds(ControlPanel.getEmbed())
                    .setComponents(ControlPanel.getButtons())
                    .queue();
            return;
        }

        String[] parts = event.getComponentId().split(EmbedConfig.RANDOM_SYMBOL_4);
        if (parts.length < 3 || !parts[Config.UNI_ZERO].equals(Config.BOT_NAME)) return;

        String action = parts[1];
        String targetIdStr = parts[2];
        long targetId = Long.parseLong(targetIdStr);

        if (event.getUser().getIdLong() != Config.OWNER_ID)
        {
            return;
        }


        event.getJDA().retrieveUserById(targetId).queue(targetUser ->
        {
            var guild = event.getJDA().getGuildById(Config.DEV_GUILD_ID);
            if (guild == null) return;

            java.util.List<Long> reporters = rawReportLog.get(targetId);

            String timeNow = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(EmbedConfig.TIME_HOUR_STAMP));
            String reason = "Breach of Protocol / Automated Action";

            guild.retrieveMember(targetUser).queue(m ->
            {
                String jDate = m.getTimeJoined().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));

                String verdictLabel = switch (action)
                {
                    case "ban" -> "TERMINATED / BANNED";
                    case "kick" -> "EXPELLED / KICKED";
                    case "clear" -> "RESOLVED / CLEARED";
                    default -> "PROCESSED";
                };

                net.dv8tion.jda.api.utils.FileUpload resolutionFile = CaseStatusRenderer.render(
                        targetUser.getName(),
                        targetUser.getEffectiveAvatarUrl(),
                        verdictLabel,
                        targetIdStr,
                        jDate,
                        timeNow,
                        reason
                );

                sendToArchive(event, targetIdStr, action, reason);

                java.util.Set<Long> heatGroup = reportHeatMap.get(targetId);

                switch (action)
                {
                    case "ban" -> guild.ban(targetUser, 7, java.util.concurrent.TimeUnit.DAYS).reason(reason).queue(s ->
                    {
                        event.getHook().editOriginal("❌ **CASE CLOSED: SUBJECT TERMINATED**").setComponents().queue();
                        if (heatGroup != null)
                        {
                            CaseStatusRenderer(event.getJDA(), targetId, resolutionFile);

                        }
                        cleanup(targetId);

                    });

                    case "kick" -> guild.kick(targetUser).reason(reason).queue(s ->
                    {
                        event.getHook().editOriginal("⚠️ **CASE CLOSED: SUBJECT EXPELLED**").setComponents().queue();
                        if (heatGroup != null)
                        {
                            CaseStatusRenderer(event.getJDA(), targetId, resolutionFile);

                        }
                        cleanup(targetId);
                    });

                    case "clear" ->
                    {
                        event.getHook().editOriginal("✅ **CASE RESOLVED: DATA PURGED**").setComponents().queue();
                        if (heatGroup != null)
                        {
                            CaseStatusRenderer(event.getJDA(), targetId, resolutionFile);
                        }
                        cleanup(targetId);

                    }
                }

            }, err ->
            {
                event.getHook().editOriginal("Subject no longer in guild. Purging records.").setComponents().queue();
                cleanup(targetId);
            });
        }, error ->
        {
            event.getHook().sendMessage("Subject no longer exists in database.").setEphemeral(true).queue();
            cleanup(targetId);
        });


    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {


        if (event.getName().equals(Config.CMD_RULES))
        {
            event.reply(BotMessages.RULES_CONTENT).setEphemeral(true).queue();
        } else if (event.getName().equals("panel"))
        {
            if (event.getUser().getIdLong() != Config.OWNER_ID) return;

            event.getUser().openPrivateChannel().queue(dm ->
            {
                dm.sendMessageEmbeds(ControlPanel.getEmbed())
                        .setComponents(ControlPanel.getButtons())
                        .queue();
            });
            event.reply("Warden Dashboard dispatched to secure DM.").setEphemeral(true).queue();
        }

        if (event.getName().equals(Config.CMD_RULES))
        {
            event.reply(BotMessages.RULES_CONTENT).setEphemeral(true).queue();
        } else if (event.getName().equals(Config.CMD_BAN))
        {
            handleBan(event);
        } else if (event.getName().equals(Config.CMD_WARN))
        {
            handleWarn(event);
        } else if (event.getName().equals(Config.CMD_UNWARN))
        {
            handleUnwarn(event);
        } else if (event.getName().equals(Config.CMD_PURGE))
        {
            handlePurge(event);
        } else if (event.getName().equals(Config.CMD_WHOIS))
        {
            handleWhois(event);
        } else if (event.getName().equals(Config.CMD_HUNTER))
        {
            handleHunter(event);
        } else if (event.getName().equals(Config.CMD_REPORT_DM))
        {
            handleReport(event);
        } else if (event.getName().equals(Config.CMD_BAN))
        {
            handleBan(event);
        }

    }


    private void cleanup(long targetId)
    {
        reportHeatMap.remove(targetId);
        rawReportLog.remove(targetId);
    }

    private void handleClosedDMs(net.dv8tion.jda.api.entities.Member target, String actionType, byte[] imageData)
    {
        var guild = target.getGuild();

        var categoryOpt = guild.getCategoriesByName(EmbedConfig.HD_HEADER_TEXT, true).stream().findFirst();

        if (categoryOpt.isPresent())
        {
            createCaseChannel(categoryOpt.get(), target, actionType, imageData);
        } else
        {
            guild.createCategory(EmbedConfig.HD_HEADER_TEXT).queue(newCategory ->
            {
                createCaseChannel(newCategory, target, actionType, imageData);
            });
        }
    }

    private void createCaseChannel(net.dv8tion.jda.api.entities.channel.concrete.Category category, net.dv8tion.jda.api.entities.Member target, String actionType, byte[] imageData)
    {
        category.createTextChannel(EmbedConfig.CSC_CASE + target.getUser().getName())
                .addMemberPermissionOverride(target.getIdLong(),
                        java.util.List.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL, net.dv8tion.jda.api.Permission.MESSAGE_HISTORY),
                        java.util.List.of(net.dv8tion.jda.api.Permission.MESSAGE_SEND))
                .addRolePermissionOverride(target.getGuild().getPublicRole().getIdLong(),
                        null,
                        java.util.List.of(net.dv8tion.jda.api.Permission.VIEW_CHANNEL))
                .queue(channel ->
                {
                    net.dv8tion.jda.api.EmbedBuilder embed = new net.dv8tion.jda.api.EmbedBuilder().setColor(Colors.REPORT_2)
                            .setTitle(EmbedConfig.CSC_REPORT)
                            .setDescription(EmbedConfig.CSC_DM_MSG)
                            .setFooter("Click the button below to acknowledge.");

                    net.dv8tion.jda.api.interactions.components.buttons.Button okBtn =
                            net.dv8tion.jda.api.interactions.components.buttons.Button.success("warden:close_case", "I UNDERSTAND");

                    channel.sendMessage(target.getAsMention() + " **Attention!**")
                            .setEmbeds(embed.build())
                            .addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(imageData, "Report.png"))
                            .setActionRow(okBtn)
                            .queue();

                    channel.delete().queueAfter(48, java.util.concurrent.TimeUnit.HOURS);
                });
    }


    private void sendToArchive(ButtonInteractionEvent event, String targetIdStr, String action, String reason)
    {
        var guild = event.getJDA().getGuildById(Config.DEV_GUILD_ID);
        if (guild == null) return;

        long tId = Long.parseLong(targetIdStr);

        java.util.Set<Long> reporterIds = reportHeatMap.get(tId);
        if (reporterIds == null || reporterIds.isEmpty()) return;

        guild.retrieveMemberById(targetIdStr).queue(suspect ->
        {
            guild.retrieveMember(event.getUser()).queue(admin ->
            {
                java.util.List<net.dv8tion.jda.api.entities.Member> reporterList = new CopyOnWriteArrayList<>();

                java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(reporterIds.size());
                for (Long rId : reporterIds)
                {
                    guild.retrieveMemberById(rId).queue(repMember ->
                    {
                        reporterList.add(repMember);
                        if (counter.decrementAndGet() == 0)
                        {
                            String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(EmbedConfig.DATE_TIME_FORMAT));

                            var archiveFile = ArchiveRenderer.render(
                                    suspect, reporterList, admin, action, reason, time
                            );

                            var channel = guild.getTextChannelById(Config.ID);
                            if (channel != null && archiveFile != null)
                            {
                                channel.sendFiles(archiveFile).queue();
                            }
                        }
                    }, err ->
                    {
                        if (counter.decrementAndGet() == 0)
                        {
                            // EVEN IF MEMBER RETRIEVAL FAILS, WE STILL RENDER WITH WHAT WE HAVE
                            String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(EmbedConfig.DATE_TIME_FORMAT));
                            var archiveFile = ArchiveRenderer.render(suspect, reporterList, admin, action, reason, time);
                            var channel = guild.getTextChannelById(Config.ID);
                            if (channel != null && archiveFile != null) channel.sendFiles(archiveFile).queue();
                        }
                    });
                }
            });
        });
    }

    /**
     * Broadcasts resolution to the heatmap.
     * cache the bytes so I don't lose the stream and stagger the DMs
     * so Discord doesn't do anything shit with my API connection. :)
     */
    private void CaseStatusRenderer(net.dv8tion.jda.api.JDA jda, long targetId, net.dv8tion.jda.api.utils.FileUpload file)
    {
        if (file == null) return;
        java.util.Set<Long> heatGroup = reportHeatMap.get(targetId);
        if (heatGroup == null || heatGroup.isEmpty()) return;

        try
        {
            byte[] imageData = file.getData().readAllBytes(); // Read ONCE for speed
            int delayMillis = 0;

            for (Long reporterId : heatGroup)
            {
                jda.retrieveUserById(reporterId).queueAfter(delayMillis, java.util.concurrent.TimeUnit.MILLISECONDS, user ->
                {
                    user.openPrivateChannel().queue(ch ->
                    {
                        ch.sendMessage(EmbedConfig.SEND_TEXT)
                                .addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(imageData, Config.CASE_STATUS_IMG_FORMAT))
                                .queue(null, err ->
                                {
                                    // IF DM FAILS:
                                    var guild = jda.getGuildById(Config.DEV_GUILD_ID);
                                    if (guild != null)
                                    {
                                        guild.retrieveMember(user).queue(m -> handleClosedDMs(m, "REPORT_PROCESSED", imageData));
                                    }
                                });
                    }, err ->
                    {

                    });
                });
                delayMillis += 350;
            }
        } catch (java.io.IOException e)
        {

        }
    }


    private void handleWhois(SlashCommandInteractionEvent event)
    {
        if (event.getUser().getIdLong() != Config.OWNER_ID)
        {
            event.reply(BotMessages.ERR_UNAUTHORIZED).setEphemeral(true).queue();
            return;
        }
        var userOption = event.getOption(Config.OPT_TARGET).getAsUser();

        event.getGuild().retrieveMember(userOption).queue(member ->
        {
            int warnCount = pm.getWarnCount(member);

            String info = String.format(EmbedConfig.WHOIS, member.getEffectiveName(), member.getId(), member.getTimeJoined().toEpochSecond(), member.getUser().getTimeCreated().toEpochSecond(), warnCount);

            event.reply(info).setEphemeral(true).queue();
        }, error -> event.reply(BotMessages.ERR_USER_NOT_FOUND).queue());
    }

    private void handlePurge(SlashCommandInteractionEvent event)
    {
        if (event.getUser().getIdLong() != Config.OWNER_ID)
        {
            event.reply(BotMessages.ERR_UNAUTHORIZED).setEphemeral(true).queue();
            return;
        }

        int amount = event.getOption(Config.OPT_AMOUNT).getAsInt();

        if (amount < 1 || amount > Config.PURGE_AMOUNT)
        {
            event.reply(Config.PURGE_WARN_MESSAGE).setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        event.getChannel().getIterableHistory()
                .takeAsync(amount)
                .thenAccept(messages ->
                {
                    event.getChannel().purgeMessages(messages);
                    event.getHook().editOriginal(Config.PURGE_SUCCESS_MESSAGE + messages.size() + Config.MESSAGE)
                            .queue();
                })
                .exceptionally(throwable ->
                {
                    event.getHook().editOriginal(Config.PURGE_FAILED_TO + throwable.getMessage()).queue();
                    return null;
                });
    }

    private void handleBan(SlashCommandInteractionEvent event)
    {
        if (event.getUser().getIdLong() != Config.OWNER_ID)
        {
            event.reply(BotMessages.ERR_UNAUTHORIZED).setEphemeral(true).queue();
            return;
        }
        var targetOption = event.getOption(Config.OPT_BAN_TARGET);
        if (targetOption == null) return;

        event.getGuild().ban(targetOption.getAsUser(), Config.UNI_ZERO, TimeUnit.DAYS)
                .queue(
                        success -> event.reply(BotMessages.MSG_BAN_SUCCESS).setEphemeral(true).queue(),
                        error -> event.reply(BotMessages.ERR_BAN_FAILED + error.getMessage()).setEphemeral(true).queue()
                );
    }

    private void handleWarn(SlashCommandInteractionEvent event)
    {
        if (event.getUser().getIdLong() != Config.OWNER_ID)
        {
            event.reply(BotMessages.ERR_UNAUTHORIZED).setEphemeral(true).queue();
            return;
        }

        var userOption = event.getOption(Config.OPT_TARGET).getAsUser();

        event.getGuild().retrieveMember(userOption).queue(member ->
        {
            int count = pm.addManualWarnOnly(member);
            String response = String.format(Config.WARN_MESSAGE,
                    member.getEffectiveName(), count);
            event.reply(response).setEphemeral(true).queue();
        }, error ->
        {
            event.reply(BotMessages.ERR_USER_NOT_FOUND).setEphemeral(true).queue();
        });
    }

    private void handleUnwarn(SlashCommandInteractionEvent event)
    {
        if (event.getUser().getIdLong() != Config.OWNER_ID)
        {
            event.reply(BotMessages.ERR_UNAUTHORIZED).setEphemeral(true).queue();
            return;
        }

        var userOption = event.getOption(Config.OPT_TARGET).getAsUser();

        event.getGuild().retrieveMember(userOption).queue(member ->
        {
            pm.resetWarns(member);
            String response = String.format(Config.UNWARN_MESSAGE, member.getEffectiveName());
            event.reply(response).setEphemeral(true).queue();
        }, error ->
        {
            event.reply(BotMessages.ERR_USER_NOT_FOUND).setEphemeral(true).queue();
        });
    }

    private void handleHunter(SlashCommandInteractionEvent event)
    {
        if (!ServiceManager.isEnabled("hunter"))
        {
            event.reply(EmbedConfig.HUNTER_EVENT_OFFLINE).setEphemeral(true).queue();
            return;
        }
        if (event.getUser().getIdLong() != Config.OWNER_ID)
        {
            event.reply(BotMessages.ERR_UNAUTHORIZED).setEphemeral(true).queue();
            return;
        }

        var targetUser = event.getOption(Config.OPT_TARGET).getAsUser();
        String phrase = event.getOption(Config.OPT_PHRASE).getAsString();
        int amount = event.getOption(Config.OPT_AMOUNT).getAsInt();

        int depth = Math.min(amount, Config.PURGE_AMOUNT);

        event.deferReply(true).queue();

        try
        {
            SanitizationEngine.targetedHunter(event.getGuildChannel(), targetUser, phrase, depth);

            event.getHook().editOriginal(EmbedConfig.HUNTER_EVENT + depth + EmbedConfig.HUNTER_EVENT_2 + targetUser.getEffectiveName() + ".").queue();
        } catch (Exception e)
        {
            event.getHook().editOriginal(EmbedConfig.HUNTER_FAILED+ e.getMessage()).queue();
        }
    }


    /**
     * TODO: Too Lazy to Remove HardCodes!
     * Well, Let's see
     * Main Thing is that it's working!!
     */
    private void handleReport(SlashCommandInteractionEvent event)
    {
        var targetOption = event.getOption(Config.OPT_TARGET);
        var reasonOption = event.getOption(EmbedConfig.REASON);

        if (targetOption == null || reasonOption == null)
        {
            event.reply(EmbedConfig.REPORT_REPLY).setEphemeral(true).queue();
            return;
        }

        net.dv8tion.jda.api.entities.User targetUser = targetOption.getAsUser();
        net.dv8tion.jda.api.entities.User reporterUser = event.getUser();
        String reason = reasonOption.getAsString();
        long targetId = targetUser.getIdLong();
        long reporterId = reporterUser.getIdLong();

        if (targetId == reporterId)
        {
            event.reply(EmbedConfig.REPORT_ERROR_1).setEphemeral(true).queue();
            return;
        }

        event.getGuild().retrieveMember(targetUser).queue(targetMember ->
        {
            event.getGuild().retrieveMember(reporterUser).queue(reporterMember ->
            {
                reportHeatMap.computeIfAbsent(targetId, k -> ConcurrentHashMap.newKeySet()).add(reporterId);
                int uniqueCount = reportHeatMap.get(targetId).size();

                rawReportLog.computeIfAbsent(targetId, k -> new CopyOnWriteArrayList<>()).add(reporterId);
                int totalAttempts = rawReportLog.get(targetId).size();

                java.util.List<String> reporterAvatars = new java.util.ArrayList<>();
                java.util.Set<Long> reporterIds = reportHeatMap.get(targetId);

                if (reporterIds != null)
                {
                    for (Long rId : reporterIds)
                    {
                        var user = event.getJDA().getUserById(rId);
                        if (user != null)
                        {
                            reporterAvatars.add(user.getEffectiveAvatarUrl());
                        } else
                        {
                            reporterAvatars.add(event.getJDA().getSelfUser().getDefaultAvatarUrl());
                        }
                    }
                }

                String joinedDate = targetMember.getTimeJoined().format(java.time.format.DateTimeFormatter.ofPattern(EmbedConfig.TIME_STAMP));
                String reportTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(EmbedConfig.TIME_HOUR_STAMP));
                String targetFullDisplay = targetMember.getEffectiveName() + EmbedConfig.RANDOM_SYMBOL_2 + targetUser.getName() + ")";

                net.dv8tion.jda.api.utils.FileUpload caseFile = ReportRenderer.render(targetFullDisplay, targetUser.getEffectiveAvatarUrl(), reporterAvatars, reason, uniqueCount, totalAttempts, joinedDate, reportTime);
                net.dv8tion.jda.api.EmbedBuilder term = new net.dv8tion.jda.api.EmbedBuilder();
                term.setColor(Colors.REPORT_1);
                term.setAuthor(EmbedConfig.WARDEN_REPORT_LOGO, null, event.getJDA().getSelfUser().getAvatarUrl());
                term.setTitle(EmbedConfig.WARDEN_UPLOAD_STATUS);
                term.setDescription(EmbedConfig.ENCRYPYION + EmbedConfig.CASE_ID + targetId + EmbedConfig.RANDOM_SYMBOL_1 + EmbedConfig.HEADER + EmbedConfig.REPORT_TEXT);
                term.setFooter(EmbedConfig.SYSTEM_NODE + event.getGuild().getName(), null);
                term.setTimestamp(java.time.Instant.now());
                event.replyEmbeds(term.build()).setEphemeral(true).queue();

                net.dv8tion.jda.api.interactions.components.buttons.Button banBtn = net.dv8tion.jda.api.interactions.components.buttons.Button.danger(EmbedConfig.WARDEN_BAN + targetId, EmbedConfig.WARDEN_BAN_BUTTON);
                net.dv8tion.jda.api.interactions.components.buttons.Button kickBtn = net.dv8tion.jda.api.interactions.components.buttons.Button.secondary(EmbedConfig.WARDEN_KICK + targetId, EmbedConfig.WARDEN_KICK_BUTTON);
                net.dv8tion.jda.api.interactions.components.buttons.Button clearBtn = net.dv8tion.jda.api.interactions.components.buttons.Button.primary(EmbedConfig.WARDEN_CLEAR + targetId, EmbedConfig.WARDEN_CLEAR_BUTTON);
                event.getJDA().retrieveUserById(Config.OWNER_ID).queue(owner ->
                {
                    owner.openPrivateChannel().queue(dm ->
                    {
                        net.dv8tion.jda.api.EmbedBuilder logEmbed = new net.dv8tion.jda.api.EmbedBuilder();
                        logEmbed.setColor(Colors.REPORT_2);
                        logEmbed.setTitle(EmbedConfig.LOG_EMBED);
                        logEmbed.setDescription(EmbedConfig.LOG_DIS + targetFullDisplay + EmbedConfig.LOG_2_DIS);
                        dm.sendMessageEmbeds(logEmbed.build()).addFiles(caseFile).addActionRow(banBtn, kickBtn, clearBtn).queue();
                    });
                });
            });
        }, error -> event.reply(EmbedConfig.EVENT_TITLE).setEphemeral(true).queue());
    }
}