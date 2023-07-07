package com.example.seg2105project;


public class Topic {
         private String topicId;
        private String topicName;
        private String yearsOfExperience;
        private String experienceDescription;

        public Topic() {
            // Default constructor required for Firebase database
        }

        public Topic(String topicId, String topicName, String yearsOfExperience, String experienceDescription) {
            this.topicId = topicId;
            this.topicName = topicName;
            this.yearsOfExperience = yearsOfExperience;
            this.experienceDescription = experienceDescription;
        }
         public String getTopicIdtId() {
        return topicId;
    }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getYearsOfExperience() {
            return yearsOfExperience;
        }

        public void setYearsOfExperience(String yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
        }

        public String getExperienceDescription() {
            return experienceDescription;
        }

        public void setExperienceDescription(String experienceDescription) {
            this.experienceDescription = experienceDescription;
        }

    }
