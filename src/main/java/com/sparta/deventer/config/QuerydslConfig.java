package com.sparta.deventer.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;

@Configuration
public class QuerydslConfig {

  private final EntityManager entityManager;

  public QuerydslConfig(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory((jakarta.persistence.EntityManager) entityManager);
  }
}