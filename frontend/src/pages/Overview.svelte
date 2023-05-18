<script>
    import axios from "axios";
    import { actualUser, jwt_token, myUserId, isAuthenticated } from "../store"; //Das JWT wird aus dem Store geladen

    const api_root = window.location.origin;

    let isLoading = false;

    let cars = [];

    function getCars() {
        var config = {
            method: "get",
            url: api_root + "/api/mycars", //Komplette URL für den Request erstellen, z.B: http://localhost:8080/api/car?pageSize=4&pageNumber=1
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                cars = response.data;
            })
            .catch(function (error) {
                alert("Konnte Fahrzeuge nicht laden.");
                console.log(error);
            });
    }
    getCars();

    function unrentCar(carId) {
        isLoading = true;
        var config = {
            method: "put",
            url: api_root + "/api/service/me/unrentcar?carId=" + carId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getCars();
            })
            .catch(function (error) {
                alert("Konnte Fahrzeug nicht entmieten.");
                console.log(error);
            })
            .finally(function () {
                isLoading = false;
            });
    }

    function deleteMyCarById(carId) {
        var config = {
            method: "delete",
            url: api_root + "/api/me/car/" + carId,
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
        };

        axios(config)
            .then(function (response) {
                alert("Dein Vehicle wurde gelöscht.");
                getCars();
            })
            .catch(function (error) {
                alert("Konnte Fahrzeug nicht löschen.");
                console.log(error);
            });
    }
</script>

{#if $isAuthenticated}
    {#if isLoading}
        <img id="loading" src={"images/design/spinner.gif"} alt="loading" />
    {:else}
        <h2>Account Details</h2>

        <div class="acc-info-container">
            <p>
                <img
                    class="rounded-circle mb-3"
                    src={$actualUser.picture}
                    alt=""
                    srcset=""
                />
            </p>
            <div>
                <br />
                <p><i><b>Benutzername:</b> {$actualUser.name}</i></p>
                <p><i><b>Email-Adresse:</b> {$actualUser.email}</i></p>
            </div>
        </div>

        <h2 style="margin-top: 10px;">Meine Vehicles</h2>
        <table class="table">
            <thead>
                <tr>
                    <th scope="col" style="width:20%">Vehicle</th>
                    <th />
                    <th scope="col">Ort</th>
                    <th scope="col">Preis</th>
                    <th scope="col">Verfügbarkeit</th>
                    <th style="width:10%" />
                </tr>
            </thead>
            {#each cars as car}
                {#if car.ownerId === $myUserId}
                    <tbody>
                        <tr>
                            <td
                                ><a href={"#/car/" + car.id}
                                    ><img
                                        id="cars-table-img"
                                        src={"images/" + car.model + ".jpg"}
                                        alt={car.model}
                                    /></a
                                ></td
                            >
                            <td
                                ><p id="cars-table-name">
                                    {car.brand}
                                    {car.model}
                                </p>
                                <p>
                                    <img
                                        src="images/design/calendar.png"
                                        alt="calendar"
                                        width="18"
                                        style="margin-right: 7px; margin-top: -3px;"
                                    />{car.year}
                                </p>
                                <p>
                                    <img
                                        src="images/design/fuel.png"
                                        alt="calendar"
                                        width="18"
                                        style="margin-right: 7px; margin-top: -3px;"
                                    />{car.carType}
                                </p>
                                <p>
                                    <img
                                        src="images/design/transmission.png"
                                        alt="calendar"
                                        width="18"
                                        style="margin-right: 7px; margin-top: -3px;"
                                    />{car.carTransmission}
                                </p>
                            </td>
                            <td>{car.carArea}</td>
                            <td>{car.price} CHF/Tag</td>
                            <td>{car.carState}</td>
                            <td>
                                <button
                                    type="button"
                                    class="btn btn-danger btn-sm"
                                    id="deleteButton"
                                    on:click={() => {
                                        deleteMyCarById(car.id);
                                    }}>Löschen</button
                                >
                            </td>
                        </tr>
                    </tbody>
                {/if}
            {/each}
        </table>
        <h2 style="margin-top: 40px;">Meine gemieteten Vehicles</h2>
        <table class="table" style="margin-bottom: 500px;">
            <thead>
                <tr>
                    <th scope="col" style="width:20%">Vehicle</th>
                    <th />
                    <th scope="col">Ort</th>
                    <th scope="col">Preis</th>
                    <th scope="col">Verfügbarkeit</th>
                    <th style="width:10%" />
                </tr>
            </thead>
            {#each cars as car}
                {#if car.userId === $myUserId}
                    <tbody>
                        <tr>
                            <td
                                ><a href={"#/car/" + car.id}
                                    ><img
                                        id="cars-table-img"
                                        src={"images/" + car.model + ".jpg"}
                                        alt={car.model}
                                    /></a
                                ></td
                            >
                            <td
                                ><p id="cars-table-name">
                                    {car.brand}
                                    {car.model}
                                </p>
                                <p>
                                    <img
                                        src="images/design/calendar.png"
                                        alt="calendar"
                                        width="18"
                                        style="margin-right: 7px; margin-top: -3px;"
                                    />{car.year}
                                </p>
                                <p>
                                    <img
                                        src="images/design/fuel.png"
                                        alt="calendar"
                                        width="18"
                                        style="margin-right: 7px; margin-top: -3px;"
                                    />{car.carType}
                                </p>
                                <p>
                                    <img
                                        src="images/design/transmission.png"
                                        alt="calendar"
                                        width="18"
                                        style="margin-right: 7px; margin-top: -3px;"
                                    />{car.carTransmission}
                                </p>
                            </td>
                            <td>{car.carArea}</td>
                            <td>{car.price} CHF/Tag</td>
                            <td>{car.carState}</td>
                            <td>
                                <button
                                    type="button"
                                    class="btn btn-sm"
                                    id="unrentButton"
                                    on:click={() => {
                                        unrentCar(car.id);
                                    }}>Miete beenden</button
                                >
                            </td>
                        </tr>
                    </tbody>
                {/if}
            {/each}
        </table>
    {/if}
{:else}
    <p>Bitte melde Dich an.</p>
{/if}
