package com.project_agh.blastradiusanalyzer.dtos;

import java.util.List;

public record ClusterDto(String name, String color, List<String> nodeIds) {}