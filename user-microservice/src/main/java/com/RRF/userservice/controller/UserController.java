package com.RRF.userservice.controller;

import com.RRF.userservice.entity.User;
import com.RRF.userservice.model.Bike;
import com.RRF.userservice.model.Car;
import com.RRF.userservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        if(users.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        if(user == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    public ResponseEntity<User> save(@RequestBody User user) {
        User userNew = userService.save(user);
        return ResponseEntity.ok(userNew);
    }

    @CircuitBreaker(name = "carsCB", fallbackMethod = "fallbackGetCars")
    @GetMapping("/cars/{userId}")
    public ResponseEntity<List<Car>> getCars(@PathVariable("userId") int userId) {
        User user = userService.getUserById(userId);
        if(user == null)
            return ResponseEntity.notFound().build();
        List<Car> cars = userService.getCars(userId);
        return ResponseEntity.ok(cars);
    }

    @CircuitBreaker(name = "carsCB", fallbackMethod = "fallbackSaveCar")
    @PostMapping("/savecar/{userId}")
    public ResponseEntity<Car> saveCar(@PathVariable("userId") int userId, @RequestBody Car car) {
        if(userService.getUserById(userId) == null)
            return ResponseEntity.notFound().build();
        Car carNew = userService.saveCar(userId, car);
        return ResponseEntity.ok(car);
    }

    @CircuitBreaker(name = "bikesCB", fallbackMethod = "fallbackGetBikes")
    @GetMapping("/bikes/{userId}")
    public ResponseEntity<List<Bike>> getBikes(@PathVariable("userId") int userId) {
        User user = userService.getUserById(userId);
        if(user == null)
            return ResponseEntity.notFound().build();
        List<Bike> bikes = userService.getBikes(userId);
        return ResponseEntity.ok(bikes);
    }

    @CircuitBreaker(name = "bikesCB", fallbackMethod = "fallbackSaveBike")
    @PostMapping("/savebike/{userId}")
    public ResponseEntity<Bike> saveBike(@PathVariable("userId") int userId, @RequestBody Bike bike) {
        if(userService.getUserById(userId) == null)
            return ResponseEntity.notFound().build();
        Bike bikeNew = userService.saveBike(userId, bike);
        return ResponseEntity.ok(bike);
    }

    @CircuitBreaker(name = "allCB", fallbackMethod = "fallbackGetAll")
    @GetMapping("/getAll/{userId}")
    public ResponseEntity<Map<String, Object>> getAllVehicles(@PathVariable("userId") int userId) {
        Map<String, Object> result = userService.getUserAndVehicles(userId);
        return ResponseEntity.ok(result);
    }

    //Métodos de los Circuit Breaker definidos cómo fallbackGetCars, fallbackSaveCar, fallbackGetBikes, fallbackSaveBike
    private ResponseEntity<List<Car>> fallbackGetCars(@PathVariable("userId") int userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tiene los coches en el taller.", HttpStatus.OK);
    }

    private ResponseEntity<Car> fallbackSaveCar(@PathVariable("userId") int userId, @RequestBody Car car, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tiene dinero para pagar el coche.", HttpStatus.OK);
    }

    private ResponseEntity<List<Bike>> fallbackGetBikes(@PathVariable("userId") int userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tiene las motos en el taller.", HttpStatus.OK);
    }

    private ResponseEntity<Bike> fallbackSaveBike(@PathVariable("userId") int userId, @RequestBody Bike bike, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tiene dinero para pagar la moto.", HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> fallbackGetAll(@PathVariable("userId") int userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tiene los vehículos en el taller.", HttpStatus.OK);
    }
}
