package ru.viktorgezz.pizza_resource_service.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.viktorgezz.pizza_resource_service.dto.rs.RestaurantDto;
import ru.viktorgezz.pizza_resource_service.model.Restaurant;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Настройка стратегии сопоставления
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Настройка маппинга для Restaurant -> RestaurantDto
        modelMapper.createTypeMap(Restaurant.class, RestaurantDto.class)
                .addMappings(mapper -> {
                    mapper.map(Restaurant::getId, RestaurantDto::setId);
                    mapper.map(Restaurant::getAddress, RestaurantDto::setAddress);
                    mapper.map(Restaurant::getStatus, RestaurantDto::setStatus);
                    mapper.map(Restaurant::getOpen, RestaurantDto::setOpeningTime);
                    mapper.map(Restaurant::getClose, RestaurantDto::setClosingTime);
                });

        return modelMapper;
    }
} 