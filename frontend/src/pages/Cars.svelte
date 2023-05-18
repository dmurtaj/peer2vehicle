<script>
    import axios from "axios";
    import { actualUser, jwt_token, myUserId, isAuthenticated } from "../store"; //Das JWT wird aus dem Store geladen
    import { querystring } from "svelte-spa-router"; //Wird benötigt, um Query-Parameter aus der aktuellen URL auszulesen, z.B.: http://localhost:8080/#/cars?pageNumber=2

    const api_root = window.location.origin; //http://localhost:8080

    let currentPage;
    let nrOfPages = 0;
    let defaultPageSize = 4;
    /*In diesen Variablen merken wir uns, welche
    Page aktuell angezeigt wird und wie viele
    Pages es insgesamt gibt.*/

    let priceMax;
    let carState;
    let carArea; //In den Input-Elementen eingetragene Werte

    let isLoading = false;

    let cars = [];

    let carAreas = [
        "Aarau",
        "Adliswil",
        "Altstätten",
        "Amriswil",
        "Arbon",
        "Baden",
        "Basel",
        "Bellinzona",
        "Bern",
        "Biel",
        "Bülach",
        "Chur",
        "Davos",
        "Dietikon",
        "Dübendorf",
        "Emmen",
        "Frauenfeld",
        "Genf",
        "Glarus",
        "Gossau",
        "Hinwil",
        "Horgen",
        "Kloten",
        "Kreuzlingen",
        "Kriens",
        "Küsnacht",
        "Lausanne",
        "Lenzburg",
        "Locarno",
        "Luzern",
        "Opfikon",
        "Rapperswil",
        "Regensdorf",
        "Romanshorn",
        "Schaffhausen",
        "Schlieren",
        "Schwyz",
        "Solothurn",
        "Thalwil",
        "Thun",
        "Uster",
        "Volketswil",
        "Wallisellen",
        "Wettingen",
        "Wetzikon",
        "Wil",
        "Winterthur",
        "Zürich",
    ];

    $: {
        let searchParams = new URLSearchParams($querystring);
        if (searchParams.has("page")) {
            currentPage = searchParams.get("page");
        } else {
            currentPage = "1";
        }
        getCars();
    }
    /* Dieser Code wird immer dann ausgeführt, wenn sich der Wert einer Variablen im Code-Block $: {... } ändert,
    siehe auch https://svelte.dev/tutorial/reactive-statements
    Wir lesen hier den Query-Parameter "page" aus der URL und holen uns anschliessend alle Cars. */

    function getCars() {
        let query =
            "?pageSize=" + defaultPageSize + " &pageNumber=" + currentPage; //Hier werden die Query-Parameter für den Request ans Backend erstellt

        if (priceMax) {
            query += "&price=" + priceMax;
        }
        if (carState && carState !== "ALL") {
            query += "&state=" + carState;
        }
        if (carArea && carArea !== "ALL") {
            query += "&carArea=" + carArea;
        }
        /* Query-Parameter für den Request ans Backend ergänzen. Beispiel für eine komplette URL:
        http://localhost:8080/api/car?pageSize=4&page=2&price=139&carType=TEST */

        var config = {
            method: "get",
            url: api_root + "/api/car" + query, //Komplette URL für den Request erstellen, z.B: http://localhost:8080/api/car?pageSize=4&pageNumber=1
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                cars = response.data.content;
                nrOfPages = response.data.totalPages; //Nach jedem Request wird die Anzahl Pages aktualisiert. Das Backend schickt diese in der Property totalPages in der Response.
            })
            .catch(function (error) {
                alert("Konnte Fahrzeuge nicht laden.");
                console.log(error);
            });
    }

    function rentCar(carId) {
        isLoading = true;
        var config = {
            method: "put",
            url: api_root + "/api/service/me/rentcar?carId=" + carId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getCars();
            })
            .catch(function (error) {
                alert("Konnte Fahrzeug nicht mieten.");
                console.log(error);
            })
            .finally(function () {
                isLoading = false;
            });
    }

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

    function deleteCar(carId) {
        var config = {
            method: "delete",
            url: api_root + "/api/car/" + carId,
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
            data: cars,
        };

        axios(config)
            .then(function (response) {
                alert("Fahrzeug gelöscht.");
                getCars();
            })
            .catch(function (error) {
                alert("Konnte Fahrzeug nicht löschen.");
                console.log(error);
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
        {#if $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
            <h2>Fahrzeugverwaltung</h2>
        {:else}
            <h2>Miete ein Vehicle</h2>
        {/if}

        <div class="filter-row my-3">
            <label for="carAreaFilter" class="col-form-label">Ort:</label>
            <select
                bind:value={carArea}
                class="form-select"
                id="carAreaFilter"
                type="text"
            >
                <option value="ALL" />
                {#each carAreas as carArea}
                    <option value={carArea}>{carArea}</option>
                {/each}
            </select>

            <label for="minPriceFilter" class="col-form-label">Preis bis:</label
            >
            <input
                class="form-control"
                type="number"
                id="minPriceFilter"
                bind:value={priceMax}
            />

            <label for="carStateFilter" class="col-form-label"
                >Verfügbarkeit:</label
            >
            <select
                bind:value={carState}
                class="form-select"
                id="carStateFilter"
                type="text"
            >
                <option value="ALL" />
                <option value="Verfügbar">Verfügbar</option>
                <option value="Besetzt">Besetzt</option>
            </select>

            <a
                class="btn"
                id="applyButton"
                href={"#/cars?page=1&carType=" +
                    carState +
                    "&price=" +
                    priceMax +
                    "&carArea=" +
                    carArea}
                role="button">Go!</a
            >
        </div>

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
            <tbody>
                {#each cars as car}
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
                            ><p id="cars-table-name">{car.brand} {car.model}</p>
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
                            {#if car.userId === $myUserId}
                                <button
                                    type="button"
                                    class="btn btn-sm"
                                    id="unrentButton"
                                    on:click={() => {
                                        unrentCar(car.id);
                                    }}>Miete beenden</button
                                >
                            {:else if car.ownerId === $myUserId}
                                <button
                                    type="button"
                                    class="btn btn-danger btn-sm"
                                    id="deleteButton"
                                    on:click={() => {
                                        deleteMyCarById(car.id);
                                    }}>Löschen</button
                                >
                            {:else if car.userId === null && car.ownerId !== $myUserId && !$actualUser.user_roles.includes("admin")}
                                <button
                                    type="button"
                                    class="btn btn-sm"
                                    id="rentButton"
                                    on:click={() => {
                                        rentCar(car.id);
                                    }}>Miete starten</button
                                >
                            {/if}
                            {#if $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
                                <button
                                    type="button"
                                    class="btn btn-danger btn-sm"
                                    id="deleteButton"
                                    on:click={() => {
                                        deleteCar(car.id);
                                    }}>Löschen</button
                                >
                            {/if}
                        </td>
                    </tr>
                {/each}
            </tbody>
        </table>
        <nav>
            <ul class="pagination">
                {#each Array(nrOfPages) as _, i}
                    <!-- In each-Blöcken kann man nur über Arrays iterieren. Wir wissen aber nur, wie viele Page-
        Links hinzugefügt werden sollen (nrOfPages). Der Trick ist, erst ein Array mit nrOfPages
        Elementen zu erstellen. Diese Elemente sind leer und interessieren uns auch nicht, darum
        bezeichnen wir sie mit _. Aber wir können den Index i im each-Block verwenden, um
        beispielsweise die Pagination-Elemente zu beschriften. //-->
                    <li class="page-item">
                        <a
                            class="page-link"
                            class:active={currentPage == i + 1}
                            href={"#/cars?page=" + (i + 1)}
                            >{i + 1}
                        </a>
                        <!-- Pagination-Element wird blau (active) angezeigt, wenn die aktuelle Page mit dem index (+1) übereinstimmt.
                    
                Bei Klick auf das Pagination-Element soll auf die entsprechende Page gewechselt werden, z.B. http://localhost:8080/#/cars?page=2//-->
                    </li>
                {/each}
            </ul>
        </nav>
    {/if}
{:else}
    <p>Bitte melde Dich an.</p>
{/if}
