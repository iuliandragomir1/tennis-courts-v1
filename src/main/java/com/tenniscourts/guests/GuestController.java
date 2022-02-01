package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/guests")
public class GuestController extends BaseRestController {

  private final GuestService guestService;

  @GetMapping
  @ApiOperation(value = "List all guests")
  public ResponseEntity<List<GuestDTO>> findAllGuests() {
    return ResponseEntity.ok(guestService.findAll());
  }

  @GetMapping("/{guestId}")
  @ApiOperation(value = "Find a guest by id")
  public ResponseEntity<GuestDTO> findGuestById(@PathVariable Long guestId) {
    return ResponseEntity.ok(guestService.findById(guestId));
  }

  @GetMapping("/guest")
  @ApiOperation(value = "Find a guest by name")
  public ResponseEntity<GuestDTO> findGuestByName(@RequestParam(value = "name") String guestName) {
    return ResponseEntity.ok(guestService.findByName(guestName));
  }

  @PutMapping
  @ApiOperation(value = "Update a guest")
  public ResponseEntity<GuestDTO> updateGuest(@RequestBody GuestDTO guestDTO) {
    return ResponseEntity.ok(guestService.update(guestDTO));
  }

  @PostMapping
  @ApiOperation(value = "Add a guest")
  public ResponseEntity<GuestDTO> addGuest(@RequestBody GuestDTO guestDTO) {
    return ResponseEntity.ok(guestService.add(guestDTO));
  }

  @DeleteMapping("/{guestId}")
  @ApiOperation(value = "Delete a guest")
  public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
    guestService.delete(guestId);
    return ResponseEntity.ok().build();
  }

}
