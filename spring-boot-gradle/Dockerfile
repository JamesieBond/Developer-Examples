FROM 786796192785.dkr.ecr.eu-west-1.amazonaws.com/10x/10x-openjdk-11-ssm AS build
ADD . /src
WORKDIR /src
ARG COMMIT_ID
ENV COMMIT_ID=$COMMIT_ID
ARG SONARQUBE_SCANNER_PARAMS='{}'
RUN ./gradlew clean build serviceSecurityChecks sonarqube --stacktrace bootJar --info -x :fraud-aml-manager-pact:test

FROM ${AWS_ACCOUNT_NO:-786796192785}.dkr.ecr.eu-west-1.amazonaws.com/10x/10x-openjdk-11-ssm
EXPOSE 8080
HEALTHCHECK --retries=12 --interval=10s CMD curl -s localhost:8080/health || exit 1
COPY --from=build /src/fraud-aml-manager-service/build/libs/fraud-aml-manager-service-*.jar /usr/local/bin/service.jar
COPY --from=build /src/run.sh /usr/local/bin/run.sh
RUN chmod +x /usr/local/bin/service.jar
RUN chmod +x /usr/local/bin/run.sh

ENV AQUA_SERVER=aquasec-gw.internal.10x.mylti3gh7p4x.net:3622
ADD microenforcer /bin/microenforcer
ADD policy.json /etc/aquasec/policy/policy.json
ADD firewall.json /etc/aquasec/policy/firewall.json
ENV AQUA_SET_USER=false
RUN chmod +x /bin/microenforcer

#CMD ["chamber", "exec", "fraud-aml-manager", "--", "/usr/local/bin/run.sh"]

ENTRYPOINT ["/usr/local/bin/run.sh"]