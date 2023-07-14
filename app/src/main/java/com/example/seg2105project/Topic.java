package com.example.seg2105project;


public class Topic {
         private String topicId;
        private String topicName;
        private String yearsOfExperience;
        private String experienceDescription;

        private boolean isOffered;

        public Topic() {
            // Default constructor required for Firebase database
        }

        public Topic(String topicId, String topicName, String yearsOfExperience, String experienceDescription) {
            this.topicId = topicId;
            this.topicName = topicName;
            this.yearsOfExperience = yearsOfExperience;
            this.experienceDescription = experienceDescription;
            this.isOffered = false;
        }
         public String getTopicId() {
        return topicId;
    }

        public String getTopicName() {
            return topicName;
        }

        public boolean getIsOffered() {
            return isOffered;
        }
        public void offered(){
            this.isOffered=true;
        }

        public String getYearsOfExperience() {
            return yearsOfExperience;
        }



        public String getExperienceDescription() {
            return experienceDescription;
        }



    }
