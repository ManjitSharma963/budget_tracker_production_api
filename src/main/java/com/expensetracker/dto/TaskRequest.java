package com.expensetracker.dto;

import com.expensetracker.entity.Task;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    private String title;

    private String subtitle;

    private LocalDate date;

    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "Start time must be in HH:MM format (24-hour)")
    private String startTime;

    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "End time must be in HH:MM format (24-hour)")
    private String endTime;

    private Task.TaskStatus status;

    public Task toTask() {
        Task task = new Task();
        task.setTitle(this.title);
        task.setSubtitle(this.subtitle);
        task.setDate(this.date);
        task.setStartTime(this.startTime);
        task.setEndTime(this.endTime);
        task.setStatus(this.status != null ? this.status : Task.TaskStatus.pending);
        return task;
    }
}

