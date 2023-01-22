package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {
    private final PlayersService playerService;

    public PlayerController(@Autowired PlayersService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getPlayers(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String title,
                                                   @RequestParam(required = false) Race race,
                                                   @RequestParam(required = false) Profession profession,
                                                   @RequestParam(required = false) Long after,
                                                   @RequestParam(required = false) Long before,
                                                   @RequestParam(required = false) Boolean banned,
                                                   @RequestParam(required = false) Integer minExperience,
                                                   @RequestParam(required = false) Integer maxExperience,
                                                   @RequestParam(required = false) Integer minLevel,
                                                   @RequestParam(required = false) Integer maxLevel,
                                                   @RequestParam(required = false, defaultValue = "ID") PlayerOrder order,
                                                   @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                   @RequestParam(required = false, defaultValue = "3") Integer pageSize) {

        SearchFilter searchFilter = new SearchFilter(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);

        List<Player> players = playerService.findAll(searchFilter, order, pageNumber, pageSize);

        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) Race race,
                                            @RequestParam(required = false) Profession profession,
                                            @RequestParam(required = false) Long after,
                                            @RequestParam(required = false) Long before,
                                            @RequestParam(required = false) Boolean banned,
                                            @RequestParam(required = false) Integer minExperience,
                                            @RequestParam(required = false) Integer maxExperience,
                                            @RequestParam(required = false) Integer minLevel,
                                            @RequestParam(required = false) Integer maxLevel) {
        SearchFilter searchFilter = new SearchFilter(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);

        Integer count = playerService.getAllCount(searchFilter);
        if (count == null) {
            return new ResponseEntity<>(count, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable(name = "id") Long id) {
        if (wrongId(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player foundPlayer = playerService.findById(id);
        if (foundPlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foundPlayer, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player newPlayer = playerService.add(player);
        if (newPlayer == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(newPlayer, HttpStatus.OK);
    }

    @PostMapping("{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable(name = "id") Long id,
                                               @RequestBody Player player) {
        if (wrongId(id) || wrongBirthdayOrExperience(player)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player updatedPlayer = playerService.update(player, id);
        if (updatedPlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }

    private boolean wrongId(Long id) {
        return id <= 0;
    }

    private boolean wrongBirthdayOrExperience(Player player) {
        Integer exp = player.getExperience();
        return (exp != null && (exp < 0 || exp > 10_000_000))
                || (player.getBirthday() != null && player.getBirthday().getTime() < 0);
    }
}

