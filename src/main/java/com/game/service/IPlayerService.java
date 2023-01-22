package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.controller.SearchFilter;
import com.game.entity.Player;

import java.util.List;

public interface IPlayerService {
    List<Player> findAll(SearchFilter searchFilter, PlayerOrder order, Integer pageNumber, Integer pageSize);

    Integer getAllCount(SearchFilter searchFilter);

    Player add(Player player);

    Player update(Player player, Long id);

    boolean remove(Long id);

    Player findById(Long id);
}
