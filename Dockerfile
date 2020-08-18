FROM phase4-scsb-base as builder
WORKDIR application
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} phase4-scsb-etl.jar
RUN java -Djarmode=layertools -jar phase4-scsb-etl.jar extract

FROM phase4-scsb-base

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/phase4-scsb-etl.jar/ ./
ENTRYPOINT java -jar -Denvironment=$ENV phase4-scsb-etl.jar && bash
