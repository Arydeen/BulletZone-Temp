package edu.unh.cs.cs619.bulletzone.web;

import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;

import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;

@RestController
@RequestMapping(value = "/games/account")
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final DataRepository data;

    @Autowired
    public AccountController(DataRepository repo) {
        this.data = repo;
    }

    @Autowired
    private DataRepository repository;

    /**
     * Handles a PUT request to register a new user account
     *
     * @param name     The username
     * @param password The password
     * @return a response w/ success boolean
     */
    @RequestMapping(method = RequestMethod.PUT, value = "register/{name}/{password}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<BooleanWrapper> register(@PathVariable String name, @PathVariable String password) {
        log.debug("Register '" + name + "' with password '" + password + "'");
        GameUser user = data.validateUser(name, password, true);
        boolean success = user != null;
        return new ResponseEntity<>(new BooleanWrapper(success), success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles a PUT request to login a user
     *
     * @param name     The username
     * @param password The password
     * @return a response w/ the user ID (or -1 if invalid)
     */
    @RequestMapping(method = RequestMethod.PUT, value = "login/{name}/{password}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<LongWrapper> login(@PathVariable String name, @PathVariable String password) {
        log.debug("Login '" + name + "' with password '" + password + "'");
        GameUser user = data.validateUser(name, password, false);
        if (user != null) {
            return new ResponseEntity<>(new LongWrapper(user.getId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new LongWrapper(-1L), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "balance/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Double> getBalance(@PathVariable int userId) {
        log.debug("Get balance for user ID: " + userId);
        double balance = data.getUserBalance(userId);
        if (balance < 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PutMapping("/balance/{userId}/deduct/{amount}")
    @ResponseBody
    public BooleanWrapper deductBalance(
            @PathVariable("userId") long userId,
            @PathVariable("amount") double amount) {
        log.debug("Deduct balance called for user {} amount {}", userId, amount);
        try {
            double currentBalance = repository.getUserBalance(userId);
            log.debug("Current balance for user {}: {}", userId, currentBalance);

            if (currentBalance < amount) {
                log.debug("Insufficient balance for user {}: {} < {}", userId, currentBalance, amount);
                return new BooleanWrapper(false);
            }

            boolean success = repository.deductUserBalance(userId, amount);
            log.debug("Deduction result for user {}: {}", userId, success);

            if (success) {
                double newBalance = repository.getUserBalance(userId);
                log.debug("New balance for user {}: {}", userId, newBalance);
            }

            return new BooleanWrapper(success);
        } catch (Exception e) {
            log.error("Error deducting balance for user {}: {}", userId, e.getMessage(), e);
            return new BooleanWrapper(false);
        }
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "AccountController is working";
    }
}