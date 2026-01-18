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
import org.example.stolikonline1.dto.AddRestaurantDto;
import org.example.stolikonline1.dto.ShowRestaurantInfoDto;
import org.example.stolikonline1.dto.ShowDetailedRestaurantInfoDto;
import org.example.stolikonline1.models.entities.Restaurant;
import org.example.stolikonline1.models.exceptions.RestaurantNotFoundException;
import org.example.stolikonline1.repositories.RestaurantRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper mapper;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, ModelMapper mapper) {
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
        log.info("RestaurantServiceImpl инициализирован");
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public ShowRestaurantInfoDto addRestaurant(AddRestaurantDto restaurantDto) {
        log.debug("Добавление нового ресторана: {}", restaurantDto.getName());

        // Проверка валидности рейтинга
        if (restaurantDto.getRating() < 0.0 || restaurantDto.getRating() > 5.0) {
            throw new RuntimeException("Рейтинг должен быть в диапазоне от 0.0 до 5.0!");
        }

        // Проверка валидности ценового уровня
        if (restaurantDto.getPriceLevel() < 1 || restaurantDto.getPriceLevel() > 3) {
            throw new RuntimeException("Уровень цен должен быть от 1 до 3!");
        }

        // Проверка времени работы
        if (restaurantDto.getOpeningTime() != null && restaurantDto.getClosingTime() != null) {
            try {
                LocalTime opening = LocalTime.parse(restaurantDto.getOpeningTime());
                LocalTime closing = LocalTime.parse(restaurantDto.getClosingTime());
                if (closing.isBefore(opening) && !closing.equals(LocalTime.MIDNIGHT)) {
                    throw new RuntimeException("Время закрытия должно быть после времени открытия!");
                }
            } catch (Exception e) {
                throw new RuntimeException("Некорректный формат времени!");
            }
        }

        // Проверка вместимости
        if (restaurantDto.getMaxCapacity() < 1 || restaurantDto.getMaxCapacity() > 1000) {
            throw new RuntimeException("Вместимость должна быть от 1 до 1000 человек!");
        }

        Restaurant restaurant = mapper.map(restaurantDto, Restaurant.class);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        log.info("Ресторан успешно добавлен: {}", restaurant.getName());
        return mapper.map(savedRestaurant, ShowRestaurantInfoDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"restaurants", "restaurant"}, allEntries = true)
    public void deleteRestaurant(String name) {
        log.debug("Удаление ресторана: {}", name);

        if (!restaurantRepository.existsByName(name)) {
            log.warn("Попытка удалить несуществующий ресторан: {}", name);
            throw new RestaurantNotFoundException("Ресторан с именем '" + name + "' не найден");
        }

        restaurantRepository.deleteByName(name);
        log.info("Ресторан успешно удален: {}", name);
    }

    @Override
    @Cacheable(value = "restaurant", key = "#name", unless = "#result == null")
    public ShowDetailedRestaurantInfoDto getRestaurantDetails(String name) {
        log.debug("Получение деталей ресторана: {}", name);

        Restaurant restaurant = restaurantRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("Ресторан не найден: {}", name);
                    return new RestaurantNotFoundException("Ресторан с именем '" + name + "' не найден");
                });

        return mapper.map(restaurant, ShowDetailedRestaurantInfoDto.class);
    }

    @Override
    public Page<ShowRestaurantInfoDto> getAllRestaurants(Pageable pageable) {
        log.debug("Получение ресторанов с пагинацией: страница {}, размер {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return restaurantRepository.findAll(pageable)
                .map(restaurant -> mapper.map(restaurant, ShowRestaurantInfoDto.class));
    }

    @Override
    @Cacheable(value = "restaurants", key = "'search-' + #searchTerm")
    public List<ShowRestaurantInfoDto> searchRestaurants(String searchTerm) {
        log.debug("Поиск ресторанов по запросу: {}", searchTerm);

        List<ShowRestaurantInfoDto> results = restaurantRepository
                .searchByNameCuisineOrAddress(searchTerm)
                .stream()
                .map(restaurant -> mapper.map(restaurant, ShowRestaurantInfoDto.class))
                .collect(Collectors.toList());

        log.info("По запросу '{}' найдено ресторанов: {}", searchTerm, results.size());
        return results;
    }

    @Override
    @Cacheable(value = "restaurants", key = "'cuisine-' + #cuisine")
    public List<ShowRestaurantInfoDto> filterByCuisine(String cuisine) {
        log.debug("Фильтрация ресторанов по кухне: {}", cuisine);

        return restaurantRepository.findByCuisine(cuisine)
                .stream()
                .map(restaurant -> mapper.map(restaurant, ShowRestaurantInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "restaurants", key = "'price-' + #priceLevel")
    public List<ShowRestaurantInfoDto> filterByPriceLevel(Integer priceLevel) {
        log.debug("Фильтрация ресторанов по уровню цен: {}", priceLevel);

        return restaurantRepository.findByPriceLevel(priceLevel)
                .stream()
                .map(restaurant -> mapper.map(restaurant, ShowRestaurantInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "restaurants", key = "'rating-' + #minRating")
    public List<ShowRestaurantInfoDto> filterByRatingGreaterThan(Double minRating) {
        log.debug("Фильтрация ресторанов по рейтингу выше: {}", minRating);

        return restaurantRepository.findByRatingGreaterThanEqual(minRating)
                .stream()
                .map(restaurant -> mapper.map(restaurant, ShowRestaurantInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowRestaurantInfoDto> findByTown(String town) {
        log.debug("Поиск ресторанов в городе: {}", town);
        return searchRestaurants(town);
    }

    @Override
    public List<ShowRestaurantInfoDto> findByBudgetGreaterThan(Double minBudget) {
        log.debug("Поиск ресторанов с бюджетом больше: {}", minBudget);
        return List.of();
    }

    @Override
    @Cacheable(value = "restaurants", key = "'all'")
    public List<ShowRestaurantInfoDto> allRestaurants() {
        log.debug("Получение списка всех ресторанов");
        List<ShowRestaurantInfoDto> restaurants = restaurantRepository.findAll().stream()
                .map(restaurant -> mapper.map(restaurant, ShowRestaurantInfoDto.class))
                .collect(Collectors.toList());
        log.info("Найдено ресторанов: {}", restaurants.size());
        return restaurants;
    }

    @Override
    @Cacheable(value = "cuisines", key = "'all'")
    public List<String> getAllCuisines() {
        log.debug("Получение всех типов кухни");
        return restaurantRepository.findAllDistinctCuisines();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"restaurant", "restaurants"}, key = "#restaurantId")
    public void updateRestaurantRating(String restaurantId) {
        log.debug("Обновление рейтинга ресторана: {}", restaurantId);
        // Логика для пересчета среднего рейтинга на основе отзывов
        // Этот метод будет вызываться при добавлении нового отзыва
    }
}
