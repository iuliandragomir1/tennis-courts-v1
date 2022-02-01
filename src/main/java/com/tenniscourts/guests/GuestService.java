package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GuestService {

  private final GuestRepository guestRepository;

  @Getter
  private final GuestMapper guestMapper;

  public GuestDTO findById(Long guestId) {
    return guestRepository.findById(guestId).map(guestMapper::map).<EntityNotFoundException>orElseThrow(() -> {
      throw new EntityNotFoundException("Guest with id = " + guestId + " was not found");
    });
  }

  public GuestDTO findByName(String guestName) {
    return guestRepository.findByName(guestName).map(guestMapper::map).<EntityNotFoundException>orElseThrow(() -> {
      throw new EntityNotFoundException("Guest with name = " + guestName + " was not found");
    });
  }

  public List<GuestDTO> findAll() {
    return guestMapper.map(guestRepository.findAll());
  }

  public GuestDTO add(GuestDTO guestDTO) {
    return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
  }

  public GuestDTO update(GuestDTO guestDTO) {
    findById(guestDTO.getId());
    Guest guest = guestRepository.save(guestMapper.map(guestDTO));

    return guestMapper.map(guest);
  }

  public void delete(Long guestId) {
    findById(guestId);
    guestRepository.deleteById(guestId);
  }

}
