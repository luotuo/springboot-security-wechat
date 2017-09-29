package com.jwcq.global;

/**
 * Created by luotuo on 17-6-7.
 */
public class Constant {
    public enum ResourceType {
        PROJECT("project"),
        TASK("task"),
        REPORT("report");

        public String getTypeName() {
            return typeName;
        }

        private String typeName;
        ResourceType(String typeName) {
            this.typeName = typeName;
        }
    }

    public enum ControllerType {
        GET("get"),
        EDIT("edit"),
        DELETE("delete"),
        ADD("add");

        public String getTypeName() {
            return typeName;
        }

        private String typeName;
        ControllerType(String typeName) { this.typeName = typeName; }
    }

    public enum UserRoleType {
        AUDITMAN("audit_man"),
        AUDITLEADER("audit_leader"),
        PROJECTMANAGER("project_manager"),
        BUSINESSMANAGER("business_manager"),
        REVIEWER("reviewer");
        public String getTypeName() {
            return typeName;
        }

        private String typeName;
        UserRoleType(String typeName) { this.typeName = typeName; }
    }
}
