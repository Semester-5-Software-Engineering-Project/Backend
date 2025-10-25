package com.tutoring.Tutorverse.Model;


import java.util.List;

public class GeminiResponse {
    private List<Candidate> candidates;

    public GeminiResponse() {}

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

    public String getText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate c = candidates.get(0);
            if (c != null && c.getContent() != null && c.getContent().getParts() != null && !c.getContent().getParts().isEmpty()) {
                Part p = c.getContent().getParts().get(0);
                if (p != null && p.getText() != null) return p.getText();
            }
        }
        return "";
    }

    public static class Candidate {
        private Content content;
        public Candidate() {}
        public Content getContent() { return content; }
        public void setContent(Content content) { this.content = content; }
    }

    public static class Content {
        private List<Part> parts;
        public Content() {}
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }

    public static class Part {
        private String text;
        public Part() {}
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}