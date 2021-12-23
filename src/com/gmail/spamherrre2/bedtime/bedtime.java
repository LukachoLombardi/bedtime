package com.gmail.spamherrre2.bedtime;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class bedtime extends JavaPlugin {

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();

        ArrayList<String> players = (ArrayList<String>) this.getConfig().getStringList("players");
        ArrayList<String> bedtimes = (ArrayList<String>) this.getConfig().getStringList("bedtimes");
        ArrayList<String> waketimes = (ArrayList<String>) this.getConfig().getStringList("waketimes");
        if(!(players.size() == bedtimes.size() && bedtimes.size() == waketimes.size()))
        {
            getLogger().severe("config list sizes not coherent!");
            getServer().getPluginManager().disablePlugin(this);
        }
        getServer().getPluginManager().registerEvents(new greetingBedtime(players, bedtimes, waketimes), this);
        getLogger().info("bedtime plugin enabled");
        scheduleBedtimes(players, bedtimes, waketimes);

        scheduleBedtimeScheduling(players, bedtimes, waketimes);
    }
    public void scheduleBedtimeScheduling(ArrayList<String> players, ArrayList<String> bedtimes, ArrayList<String> waketimes)
    {
        Date midnight = new Date();
        midnight.setHours(0);
        midnight.setMinutes(0);
        midnight.setSeconds(0);
        midnight.setDate(new Date().getDate() + 1);
        long timeUntilMidnight = midnight.getTime() - new Date().getTime();
        getLogger().info("bedtimes will be set again in around " + TimeUnit.MILLISECONDS.toHours(timeUntilMidnight) + " hours (at midnight)");
        //getLogger().info(String.valueOf(timeUntilMidnight));
        //getLogger().info(String.valueOf(timeUntilMidnight * 0.02));

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            public void run() {
                scheduleBedtimes(players, bedtimes, waketimes);
                scheduleBedtimeScheduling(players, bedtimes, waketimes);
            }
        }, (long)(timeUntilMidnight / 0.02));
    }

    public void scheduleBedtimes(ArrayList<String> players, ArrayList<String> bedtimes, ArrayList<String> waketimes)
    {
        for(String player: players)
        {
            int index = players.indexOf(player);
            scheduleBedtime(player, bedtimes.get(index), waketimes.get(index));
        }
    }

    @Override
    public void onDisable()
    {
        getLogger().info("goodbye UwU");
    }

    public void scheduleBedtime(String playername, String bedtime, String waketime)
    {
        getLogger().info("scheduling bedtime for " + playername + " " + bedtime + ", " + waketime);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date bedDate = new Date();
        Date wakeDate = new Date();
        Date currentDate = new Date();
        try {
            bedDate.setHours(format.parse(bedtime).getHours());
            bedDate.setMinutes(format.parse(bedtime).getMinutes());
            bedDate.setSeconds(format.parse(waketime).getSeconds());

            wakeDate.setHours(format.parse(waketime).getHours());
            wakeDate.setMinutes(format.parse(waketime).getMinutes());
            wakeDate.setSeconds(format.parse(waketime).getSeconds());
        } catch (ParseException e) {
            getLogger().severe("exception while parsing dates");
            getServer().getPluginManager().disablePlugin(this);
        }
        if(wakeDate.getTime() < bedDate.getTime())
        {
            wakeDate.setDate(currentDate.getDate() + 1);
        }
        long bedWait = bedDate.getTime() - currentDate.getTime();

        //scheduling bedtime ban
        final Date wake = wakeDate;
        getLogger().info("bedtime ban will occur in around " + TimeUnit.MILLISECONDS.toHours(bedWait) + " hours");
        Runnable bedtimeAction = new Runnable() {
            @Override
            public void run() {
                Bukkit.getBanList(BanList.Type.NAME).addBan(playername,
                        "go to bed! you can play again at " + waketime, wake, "bedtime");
                try
                {
                    Objects.requireNonNull(Bukkit.getPlayer(playername)).kickPlayer("It's your bedtime");
                }
                catch(NullPointerException e)
                {
                    getLogger().info("player " + playername + " isn't online and can't be kicked");
                }
            }
        };
        /* getLogger().info(bedDate.toString());
        getLogger().info(wakeDate.toString());
        getLogger().info(currentDate.toString()); */
        if(((wakeDate.getTime() >= currentDate.getTime()) &&
                (currentDate.getTime() >= bedDate.getTime())) ||
                ((wakeDate.getTime() - TimeUnit.DAYS.toMillis(1) >= currentDate.getTime()) &&
                        (currentDate.getTime() >= bedDate.getTime() - TimeUnit.DAYS.toMillis(1))))
        {
            Bukkit.getBanList(BanList.Type.NAME).addBan(playername,
                    "go to bed! you can play again at " + waketime, wake, "bedtime");
            try
            {
                Objects.requireNonNull(Bukkit.getPlayer(playername)).kickPlayer("It's your bedtime, go to bed!");
            }
            catch(NullPointerException e)
            {
                getLogger().info("player " + playername + " isn't online and can't be kicked");
            }
        } else
        {
            this.getServer().getScheduler().scheduleSyncDelayedTask(this, bedtimeAction, (long)(bedWait * 0.02));
        }
    }
}
