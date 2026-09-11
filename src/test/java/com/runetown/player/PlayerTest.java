package com.runetown.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void newPlayerShouldStartAtLevelOneWithZeroExp() {
        Player player = new Player("P001", "Kady");

        assertEquals("P001", player.getPlayerId());
        assertEquals("Kady", player.getName());
        assertEquals(1, player.getLevel());
        assertEquals(0, player.getExp());
    }

    @Test
    void playerShouldGainExp() {
        Player player = new Player("P001", "Kady");

        player.gainExp(50);

        assertEquals(50, player.getExp());
    }

    @Test
    void playerShouldLevelUpWhenExpReachesOneHundred() {
    Player player = new Player("P001", "Kady");

    player.gainExp(120);

    assertEquals(2, player.getLevel());
    assertEquals(20, player.getExp());
}
}