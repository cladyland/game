package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.controller.SearchFilter;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.IPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        if (parameterIsNull(player)) return null;
        if (wrongParameters(player)) return null;

        if (player.getBanned() == null) player.setBanned(false);
        player.setLevel(currentLevel(player));
        player.setUntilNextLevel(expToNextLevel(player));

        return repository.saveAndFlush(player);
    }

    @Override
    public Player update(Player player, Long id) {
        Player updatePlayer = findById(id);
        if (updatePlayer == null) return null;

        String name = player.getName();
        String title = player.getTitle();
        Race race = player.getRace();
        Profession profession = player.getProfession();
        Date birthday = player.getBirthday();
        Boolean banned = player.getBanned();
        Integer experience = player.getExperience();

        if (name != null) updatePlayer.setName(name);
        if (title != null) updatePlayer.setTitle(title);
        if (race != null) updatePlayer.setRace(race);
        if (profession != null) updatePlayer.setProfession(profession);
        if (birthday != null) updatePlayer.setBirthday(birthday);
        if (banned != null) updatePlayer.setBanned(banned);
        if (experience != null) updatePlayer.setExperience(experience);

        updatePlayer.setLevel(currentLevel(updatePlayer));
        updatePlayer.setUntilNextLevel(expToNextLevel(updatePlayer));

        return updatePlayer;
    }

    @Override
    public boolean remove(Long id) {
        Player player = findById(id);
        if (player == null) return false;
        repository.delete(player);
        return true;
    }

    @Override
    public Player findById(Long id) {
        return repository.findById(id).orElse(null);
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

    private boolean parameterIsNull(Player player) {
        return player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null;
    }

    private boolean wrongParameters(Player player) {
        int nameLength = player.getName().length();

        if (nameLength < 1 || nameLength > 12) return true;
        if (player.getTitle().length() > 30) return true;
        if (player.getExperience() < 0 || player.getExperience() > 10_000_000) return true;
        if (player.getBirthday().getTime() < 0) return true;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(player.getBirthday());
        int birthYear = calendar.get(Calendar.YEAR);

        if (birthYear < 2_000 || birthYear > 3_000) return true;

        return false;
    }

    private int currentLevel(Player player) {
        int exp = player.getExperience();
        return (int) (Math.sqrt(2500 + 200 * exp) - 50) / 100;
    }

    private int expToNextLevel(Player player){
        int lvl = player.getLevel();
        int exp = player.getExperience();
        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }

}
