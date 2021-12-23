package com.gmail.spamherrre2.bedtime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;


public class greetingBedtime implements Listener{

    ArrayList<String> players;
    ArrayList<String> bedtimes;
    ArrayList<String> waketimes;
    greetingBedtime(ArrayList<String> players, ArrayList<String> bedtimes, ArrayList<String> waketimes)
    {
        this.players = players;
        this.bedtimes = bedtimes;
        this.waketimes = waketimes;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {
        if(players.contains(event.getPlayer().getName()))
        {
            event.getPlayer().sendMessage(String.format("welcome, %s. Your bedtime is set from %s to %s :)",
                    event.getPlayer().getName(), bedtimes.get(players.indexOf(event.getPlayer().getName())),
                    waketimes.get(players.indexOf(event.getPlayer().getName()))));
        }
    }
}
