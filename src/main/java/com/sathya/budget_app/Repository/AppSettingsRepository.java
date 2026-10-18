package com.sathya.budget_app.Repository;

import com.sathya.budget_app.Entity.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
