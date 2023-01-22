package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.controller.SearchFilter;
import com.game.entity.Player;
import com.game.repository.IPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayersService implements IPlayerService {
    private final IPlayerRepository repository;

    public PlayersService(@Autowired IPlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Player> findAll(SearchFilter searchFilter, PlayerOrder order, Integer pageNumber, Integer pageSize) {
        Sort.Order playerOrder = Sort.Order.by(order.getFieldName());
        List<Player> players = repository.findAll(Sort.by(playerOrder));
        players = filtered(players, searchFilter);
        int firstPlayer = getFirstResultIndex(pageNumber, pageSize);
        int lastPlayer = getLastResultIndex(firstPlayer, pageSize);

        if (lastPlayer > players.size()) {
            lastPlayer = players.size();
        }

        return players.subList(firstPlayer, lastPlayer);
    }

    @Override
    public Integer getAllCount(SearchFilter searchFilter) {
        List<Player> players = repository.findAll();
        players = filtered(players, searchFilter);
        return players.size();
    }

    @Override
    public Player add(Player player) {
        return null;
    }

    @Override
    public Player update(Player player, Long id) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public Player findById(Long id) {
        return null;
    }

    private List<Player> filtered(List<Player> players, SearchFilter searchFilter) {
        List<Player> filteredList = new ArrayList<>();

        players.forEach(player -> {
                    if (searchFilter.getName() != null && !player.getName().contains(searchFilter.getName())) return;
                    if (searchFilter.getTitle() != null && !player.getTitle().contains(searchFilter.getTitle())) return;
                    if (searchFilter.getRace() != null && !player.getRace().equals(searchFilter.getRace())) return;
                    if (searchFilter.getProfession() != null && !player.getProfession().equals(searchFilter.getProfession()))
                        return;
                    if (searchFilter.getAfter() != null && player.getBirthday().getTime() < searchFilter.getAfter()) return;
                    if (searchFilter.getBefore() != null && player.getBirthday().getTime() > searchFilter.getBefore()) return;
                    if (searchFilter.getBanned() != null && !player.getBanned().equals(searchFilter.getBanned())) return;
                    if (searchFilter.getMinExperience() != null && player.getExperience() < searchFilter.getMinExperience())
                        return;
                    if (searchFilter.getMaxExperience() != null && player.getExperience() > searchFilter.getMaxExperience())
                        return;
                    if (searchFilter.getMinLevel() != null && player.getLevel() < searchFilter.getMinLevel()) return;
                    if (searchFilter.getMaxLevel() != null && player.getLevel() > searchFilter.getMaxLevel()) return;

                    filteredList.add(player);
                }
        );
        return filteredList;
    }

    private Integer getFirstResultIndex(Integer pageNumber, Integer pageSize) {
        return pageNumber * pageSize;
    }

    private Integer getLastResultIndex(Integer from, Integer pageSize) {
        return from + pageSize;
    }
}
