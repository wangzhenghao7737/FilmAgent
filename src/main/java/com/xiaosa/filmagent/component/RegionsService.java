package com.xiaosa.filmagent.component;

import com.xiaosa.filmagent.entity.regions.RegionEntity;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载地区和区域编码
 */
@Component
public class RegionsService {
    private final Map<String, String> nameToAdcode = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        loadRegionsFromExcel();
    }

    private void loadRegionsFromExcel() throws Exception {
        ClassPathResource resource = new ClassPathResource("data/AMap_adcode_citycode.xlsx");
        try (InputStream is = resource.getInputStream();
            Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 跳过标题行

                String name = getCellValue(row.getCell(0));
                String adcode = getCellValue(row.getCell(1));
                String citycode = getCellValue(row.getCell(2));

                if (name.isEmpty() || adcode.isEmpty()) continue;

                RegionEntity region = new RegionEntity(name, adcode, citycode);
                registerMappings(region);
            }
        }
    }

    private void registerMappings(RegionEntity region) {
        String originalName = region.getName();
        String cleanName = originalName.replaceAll("[省市区县]$", "").trim();

        // 1. 原始名称映射
        nameToAdcode.put(originalName, region.getAdcode());

        // 2. 清洗后名称映射（"北京市" -> "北京"）
        if (!cleanName.equals(originalName)) {
            nameToAdcode.put(cleanName, region.getAdcode());
        }

        // 3. 特殊简称处理（针对自治区）
        if ("\\N".equals(region.getCitycode()) || region.getCitycode() == null) {
            String shortName = cleanName
                    .replaceAll("自治[区州]|维吾尔|壮族|回族|藏族|特别行政区", "")
                    .trim();
            if (!shortName.isEmpty() && !shortName.equals(cleanName)) {
                nameToAdcode.put(shortName, region.getAdcode());
            }
        }
    }

    public Optional<String> getAdcode(String input) {
        if (input == null || input.isBlank()) return Optional.empty();

        String key = input.replaceAll("[省市区县]$", "").trim();
        String adcode = nameToAdcode.get(key);
        if (adcode != null) return Optional.of(adcode);

        return Optional.ofNullable(nameToAdcode.get(input));
    }

    // 工具方法：获取省级 adcode（前2位+0000）
    public Optional<String> getProvinceAdcode(String input) {
        return getAdcode(input).map(code -> code.substring(0, 2) + "0000");
    }

    // 工具方法：获取市级 adcode（前4位+00）
    public Optional<String> getCityAdcode(String input) {
        return getAdcode(input).map(code -> code.substring(0, 4) + "00");
    }

    // 辅助方法：安全读取单元格值
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    return String.valueOf((long) value);
                } else {
                    return String.valueOf(value);
                }
            case BLANK:
                return "";
            default:
                return cell.toString().trim();
        }
    }
}
