package com.project_agh.blastradiusanalyzer.dtos;

/**
 * Request payload for attaching a documentation note to a microservice
 * or cluster in the topology graph.
 *
 * @param title      the title of the note
 * @param text       the body text of the note
 * @param color      the color used to render the note in the UI
 * @param targetType the type of the target entity ("SERVICE" or "CLUSTER")
 * @param targetId   the identifier of the target entity
 */
public record NoteDto(String title, String text, String color, String targetType, String targetId) {}