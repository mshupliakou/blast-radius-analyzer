package com.project_agh.blastradiusanalyzer.dtos;

import java.util.List;

/**
 * Request payload for creating a visual cluster that groups multiple
 * nodes together in the topology graph.
 *
 * @param name    the display name of the cluster
 * @param color   the color used to render the cluster in the UI (e.g., "#ff0000")
 * @param nodeIds the list of node identifiers to include in the cluster
 */
public record ClusterDto(String name, String color, List<String> nodeIds) {}