package me.whatever.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    @Autowired // 생성자가 하나만 있고, 생성자가 받아올 paramter가 하나만 있다면 Autowired 생략 가능
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event) {
        Event newEvent = this.eventRepository.save(event);
        // eventController에 있는 method인 createEvent의 eventId를 URI로 만든 것
        URI createdUri = linkTo(EventController.class).slash("{id}").toUri();
        return ResponseEntity.created(createdUri).body(event); // created를 보낼 때는 항상 URI가 있어야 한다.
    }
}
