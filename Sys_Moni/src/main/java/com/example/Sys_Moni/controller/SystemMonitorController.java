package com.example.Sys_Moni.controller;




import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.util.FormatUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SystemMonitorController {

    @GetMapping("/system-info")
    public Map<String, Object> getSystemInfo() throws InterruptedException {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        Map<String, Object> systemMetrics = new HashMap<>();

        // CPU Information
        CentralProcessor processor = hardware.getProcessor();
        systemMetrics.put("cpuName", processor.getProcessorIdentifier().getName());

        // Memory Information
        GlobalMemory memory = hardware.getMemory();
        systemMetrics.put("totalMemory", FormatUtil.formatBytes(memory.getTotal()));
        systemMetrics.put("availableMemory", FormatUtil.formatBytes(memory.getAvailable()));
        double usedMemoryPercent = ((double)(memory.getTotal() - memory.getAvailable()) / memory.getTotal()) * 100;
        systemMetrics.put("memoryUsage", String.format("%.2f%%", usedMemoryPercent));

        // Get CPU ticks before sleep
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Thread.sleep(1000);  // Sleep for 1 second to calculate CPU load
        long[] currentTicks = processor.getSystemCpuLoadTicks();

        // System CPU Load calculation
        double systemCpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        systemMetrics.put("systemCpuLoad", String.format("%.2f%%", systemCpuLoad));

        // Sensors (Temperature and Fan Speed)
        Sensors sensors = hardware.getSensors();
        double cpuTemperature = sensors.getCpuTemperature();  // Fetch CPU temperature once

// Handle CPU temperature availability
        systemMetrics.put("cpuTemperature", cpuTemperature != 0.0 ? cpuTemperature : "Not Available");

// Fan Speed
        systemMetrics.put("fanSpeed", sensors.getFanSpeeds().length > 0 ? sensors.getFanSpeeds()[0] : 0);



        return systemMetrics;
    }
}
