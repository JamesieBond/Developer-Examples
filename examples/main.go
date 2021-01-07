package main

import (
	"flag"
	"fmt"
	"net/http"
	"os"

	"bitbucket.10x.mylti3gh7p4x.net/ft5/jwt-validator/handler"
	rsaLoader "bitbucket.10x.mylti3gh7p4x.net/ft5/jwt-validator/rsa"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"go.uber.org/zap"
)

var partyAuthKeyFileLocation *string
var serviceAuthKeyFileLocation *string
var port *int

var logger, _ = zap.NewProduction()

var (
	validation_attempts = prometheus.NewCounterVec(
		prometheus.CounterOpts{
			Name: "jwt_validation_counter",
			Help: "Count of all validation attempts",
		}, []string{"status_code", "error"})
)

func init() {
	partyAuthKeyFileLocation = flag.String("party-keyfile", "/secure/party-verification-key.pem", "Used to verify Authorization header")
	serviceAuthKeyFileLocation = flag.String("service-keyfile", "/secure/service-verification-key.pem", "Used to verify X-ServiceIdentity header")
	port = flag.Int("port", 8081, "Port to run server")
	flag.Parse()

}
func main() {

	partyAuthKey, partyAuthKeyError := rsaLoader.LoadKey(*partyAuthKeyFileLocation)

	serviceAuthKey, serviceAuthKeyError := rsaLoader.LoadKey(*serviceAuthKeyFileLocation)
	handler := handler.NewHandler(partyAuthKey, serviceAuthKey, validation_attempts)

	if partyAuthKeyError != nil {
		logger.Error(partyAuthKeyError.Error())
	}

	if serviceAuthKeyError != nil {
		logger.Error(serviceAuthKeyError.Error())
	}

	if serviceAuthKeyError != nil || partyAuthKeyError != nil {
		os.Exit(1)
	}

	http.HandleFunc("/", handler)

	http.Handle("/metrics", promhttp.Handler())
	prometheus.MustRegister(validation_attempts)

	logger.Info("Server Started", zap.Int("port", *port))

	logger.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", *port), nil).Error())

}
