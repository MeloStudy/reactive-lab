package com.reactivelab.insights.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEvent {
    private String type;
    private Actor actor;
    private Repo repo;
    private Payload payload;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Actor getActor() { return actor; }
    public void setActor(Actor actor) { this.actor = actor; }

    public Repo getRepo() { return repo; }
    public void setRepo(Repo repo) { this.repo = repo; }

    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Actor {
        private String login;
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repo {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private int size; // Commits in PushEvent
        @JsonProperty("pull_request")
        private PullRequest pullRequest;

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public PullRequest getPullRequest() { return pullRequest; }
        public void setPullRequest(PullRequest pullRequest) { this.pullRequest = pullRequest; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {
        private Base base;
        public Base getBase() { return base; }
        public void setBase(Base base) { this.base = base; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Base {
        private RepoMetadata repo;
        public RepoMetadata getRepo() { return repo; }
        public void setRepo(RepoMetadata repo) { this.repo = repo; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RepoMetadata {
        private String language;
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }
}
