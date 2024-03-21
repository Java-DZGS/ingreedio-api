FROM gradle:jdk21-alpine as build
WORKDIR /workspace/app

COPY . .

RUN gradle assemble
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","pl.edu.pw.mini.ingreedio.api.Application"]
