package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
