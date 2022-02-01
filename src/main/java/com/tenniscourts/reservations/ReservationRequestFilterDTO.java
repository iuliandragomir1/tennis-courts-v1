package com.tenniscourts.reservations;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReservationRequestFilterDTO {

  @NotNull
  @ApiModelProperty(required = true)
  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

}