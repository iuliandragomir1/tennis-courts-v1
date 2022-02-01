package com.tenniscourts.reservations;

import com.tenniscourts.schedules.Schedule;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ReservationService.class)
public class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Test
    public void getRefundValueFullRefund() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);

        schedule.setStartDateTime(startDateTime);

        Assert.assertEquals(reservationService.getRefundValue(Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build()), new BigDecimal(10));
    }

    @Test
    public void getRefundValue0() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().minusHours(1);

        schedule.setStartDateTime(startDateTime);
        BigDecimal actualRefundValue = reservationService.getRefundValue(
            Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        Assert.assertThat(new BigDecimal(0), Matchers.comparesEqualTo(actualRefundValue));
    }

    @Test
    public void getRefundValue25() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);

        schedule.setStartDateTime(startDateTime);
        BigDecimal actualRefundValue = reservationService.getRefundValue(
            Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        Assert.assertThat(new BigDecimal("2.5"), Matchers.comparesEqualTo(actualRefundValue));
    }

    @Test
    public void getRefundValue50() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(10);

        schedule.setStartDateTime(startDateTime);
        BigDecimal actualRefundValue = reservationService.getRefundValue(
            Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        Assert.assertThat(new BigDecimal(5), Matchers.comparesEqualTo(actualRefundValue));
    }

    @Test
    public void getRefundValue75() {
        Schedule schedule = new Schedule();

        LocalDateTime startDateTime = LocalDateTime.now().plusHours(14);

        schedule.setStartDateTime(startDateTime);
        BigDecimal actualRefundValue = reservationService.getRefundValue(
            Reservation.builder().schedule(schedule).value(new BigDecimal(10L)).build());

        Assert.assertThat(new BigDecimal("7.5"), Matchers.comparesEqualTo(actualRefundValue));
    }

}