package org.example.stolikonline1.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.stolikonline1.dto.AddBookingDto;
import org.example.stolikonline1.dto.ShowBookingInfoDto;
import org.example.stolikonline1.models.entities.Booking;
import org.example.stolikonline1.models.entities.Restaurant;
import org.example.stolikonline1.models.entities.User;
import org.example.stolikonline1.models.enums.BookingStatus;
import org.example.stolikonline1.models.exceptions.BookingNotFoundException;
import org.example.stolikonline1.repositories.BookingRepository;
import org.example.stolikonline1.repositories.RestaurantRepository;
import org.example.stolikonline1.repositories.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              RestaurantRepository restaurantRepository,
                              UserRepository userRepository,
                              ModelMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        log.info("BookingServiceImpl инициализирован");
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"bookings", "restaurantBookings", "userBookings"}, allEntries = true)
    public ShowBookingInfoDto addBooking(AddBookingDto bookingDto, String username) {
        log.debug("Добавление нового бронирования для пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        Restaurant restaurant = restaurantRepository.findByName(bookingDto.getRestaurantName())
                .orElseThrow(() -> new RuntimeException("Ресторан не найден!"));

        LocalDateTime bookingDateTime = bookingDto.getBookingDateTime();

        // Проверка, что дата в будущем
        if (bookingDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Дата бронирования должна быть в будущем!");
        }

        // Проверка, что бронирование не более чем на год вперед
        LocalDateTime oneYearFromNow = LocalDateTime.now().plusYears(1);
        if (bookingDateTime.isAfter(oneYearFromNow)) {
            throw new RuntimeException("Бронирование возможно максимум на год вперед!");
        }

        // Проверка количества гостей
        if (bookingDto.getNumberOfGuests() < 1 || bookingDto.getNumberOfGuests() > 20) {
            throw new RuntimeException("Количество гостей должно быть от 1 до 20!");
        }

        // Проверка, что время бронирования в разумных пределах (например, с 10:00 до 23:00)
        int hour = bookingDateTime.getHour();
        if (hour < 10 || hour > 23) {
            throw new RuntimeException("Бронирование возможно только с 10:00 до 23:00!");
        }

        // Проверка, что ресторан открыт в это время
        if (!isRestaurantOpen(restaurant, bookingDateTime)) {
            throw new RuntimeException("Ресторан закрыт в выбранное время!");
        }

        // Проверка доступности
        if (!checkAvailability(restaurant.getId(), bookingDateTime, bookingDto.getNumberOfGuests())) {
            throw new RuntimeException("На выбранное время нет свободных столиков!");
        }

        Booking booking = new Booking();
        booking.setBookingDateTime(bookingDateTime);
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setSpecialRequests(bookingDto.getSpecialRequests());
        booking.setStatus(BookingStatus.PENDING.name());
        booking.setUser(user);
        booking.setRestaurant(restaurant);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Бронирование успешно создано для пользователя: {}", username);

        // Создаем DTO вручную, т.к. ModelMapper может не справиться
        ShowBookingInfoDto dto = new ShowBookingInfoDto();
        dto.setId(savedBooking.getId());
        dto.setRestaurantName(savedBooking.getRestaurant().getName());
        dto.setRestaurantId(savedBooking.getRestaurant().getId()); // String ID
        dto.setBookingDateTime(savedBooking.getBookingDateTime());
        dto.setNumberOfGuests(savedBooking.getNumberOfGuests());
        dto.setSpecialRequests(savedBooking.getSpecialRequests());
        dto.setStatus(savedBooking.getStatus());

        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"bookings", "restaurantBookings", "userBookings"}, allEntries = true)
    public void cancelBooking(String bookingId, String username) {
        log.debug("Отмена бронирования {} пользователем {}", bookingId, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено!"));

        // Проверяем, можно ли отменить (только PENDING или CONFIRMED)
        if (!booking.getStatus().equals(BookingStatus.PENDING.name()) &&
                !booking.getStatus().equals(BookingStatus.CONFIRMED.name())) {
            throw new RuntimeException("Нельзя отменить бронирование со статусом: " + booking.getStatus());
        }

        // Проверяем, что бронирование еще не прошло
        if (booking.getBookingDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Нельзя отменить прошедшее бронирование!");
        }

        booking.setStatus(BookingStatus.CANCELLED.name());
        bookingRepository.save(booking);

        log.info("Бронирование {} отменено пользователем {}", bookingId, username);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"bookings", "restaurantBookings", "userBookings"}, allEntries = true)
    public void confirmBooking(String bookingId) {
        log.debug("Подтверждение бронирования: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено!"));

        // Проверяем, что бронирование еще не прошло
        if (booking.getBookingDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Нельзя подтвердить прошедшее бронирование!");
        }

        booking.setStatus(BookingStatus.CONFIRMED.name());
        bookingRepository.save(booking);

        log.info("Бронирование {} подтверждено", bookingId);
    }

    @Override
    @Cacheable(value = "userBookings", key = "#username")
    public List<ShowBookingInfoDto> getUserBookings(String username) {
        log.debug("Получение бронирований пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден!"));

        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToShowBookingInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "restaurantBookings", key = "#restaurantName")
    public List<ShowBookingInfoDto> getRestaurantBookings(String restaurantName) {
        log.debug("Получение бронирований ресторана: {}", restaurantName);

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new RuntimeException("Ресторан не найден!"));

        return bookingRepository.findByRestaurantId(restaurant.getId())
                .stream()
                .map(this::convertToShowBookingInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkAvailability(String restaurantId, LocalDateTime dateTime, Integer numberOfGuests) {
        log.debug("Проверка доступности ресторана {} на {} для {} гостей",
                restaurantId, dateTime, numberOfGuests);

        Restaurant restaurant = restaurantRepository.findById(restaurantId) // String ID
                .orElseThrow(() -> new RuntimeException("Ресторан не найден!"));

        // Проверяем, что ресторан открыт в это время
        if (!isRestaurantOpen(restaurant, dateTime)) {
            return false;
        }

        LocalDateTime startTime = dateTime.minusHours(2);
        LocalDateTime endTime = dateTime.plusHours(2);

        Integer bookedCount = bookingRepository.countBookingsForRestaurantInTimeRange(
                restaurantId, startTime, endTime);

        // Предположим, что у ресторана есть ограничение по вместимости
        int maxCapacity = restaurant.getMaxCapacity() != null ? restaurant.getMaxCapacity() : 50;

        return (bookedCount + numberOfGuests) <= maxCapacity;
    }

    @Override
    @Cacheable(value = "bookings", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize")
    public Page<ShowBookingInfoDto> getAllBookings(Pageable pageable) {
        log.debug("Получение всех бронирований с пагинацией");
        return bookingRepository.findAll(pageable)
                .map(this::convertToShowBookingInfoDto);
    }

    private ShowBookingInfoDto convertToShowBookingInfoDto(Booking booking) {
        ShowBookingInfoDto dto = new ShowBookingInfoDto();
        dto.setId(booking.getId());
        dto.setRestaurantName(booking.getRestaurant().getName());
        dto.setRestaurantId(booking.getRestaurant().getId()); // String ID
        dto.setBookingDateTime(booking.getBookingDateTime());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    private boolean isRestaurantOpen(Restaurant restaurant, LocalDateTime dateTime) {
        try {
            if (restaurant.getOpeningTime() == null || restaurant.getClosingTime() == null) {
                return true; // Если время не указано, считаем что открыто
            }

            String[] opening = restaurant.getOpeningTime().split(":");
            String[] closing = restaurant.getClosingTime().split(":");

            int openHour = Integer.parseInt(opening[0]);
            int openMinute = Integer.parseInt(opening[1]);
            int closeHour = Integer.parseInt(closing[0]);
            int closeMinute = Integer.parseInt(closing[1]);

            LocalTime openTime = LocalTime.of(openHour, openMinute);
            LocalTime closeTime = LocalTime.of(closeHour, closeMinute);
            LocalTime currentTime = LocalTime.of(dateTime.getHour(), dateTime.getMinute());

            // Если ресторан работает после полуночи
            if (closeTime.isBefore(openTime)) {
                return !currentTime.isBefore(openTime) || !currentTime.isAfter(closeTime);
            } else {
                return !currentTime.isBefore(openTime) && !currentTime.isAfter(closeTime);
            }
        } catch (Exception e) {
            log.warn("Ошибка при проверке времени работы ресторана: {}", e.getMessage());
            return true; // В случае ошибки считаем что открыто
        }
    }
}