package org.grupo_h.comun.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class PeriodoColaboracion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
