<script>
    import axios from "axios";
    import { actualUser, jwt_token, myUserId, isAuthenticated } from "../store"; //Das JWT wird aus dem Store geladen
    import { push } from "svelte-spa-router";

    const api_root = window.location.origin;

    let carDetails = [];
    let mapUrl = "";
    let showUpdateForm = false;
    let isLoading = false;

    let brands = [
        "Audi",
        "Citroen",
        "Fiat",
        "Ford",
        "Jeep",
        "Mini",
        "Opel",
        "Seat",
        "Tesla",
        "VW",
    ];
    let models = {
        Audi: ["A3", "A5", "A6", "Q5", "Q8", "etron"],
        Citroen: ["Berlingo", "C3", "C4", "C5", "C6", "C8"],
        Fiat: ["Doblo", "Ducato", "Panda", "Punto", "Tipo"],
        Ford: ["EcoSport", "Fiesta", "Focus", "Kuga", "Mondeo", "Mustang"],
        Jeep: [
            "Cherokee",
            "Compass",
            "Patriot",
            "Renegade",
            "Wagoneer",
            "Wrangler",
        ],
        Mini: ["Cabrio", "Clubman", "Cooper", "Countryman", "Coupe", "Paceman"],
        Opel: ["Astra", "Cascada", "Corsa", "Crossland", "Insignia", "Mokka"],
        Seat: ["Alhambra", "Arona", "Ateca", "Ibiza", "Leon", "Tarraco"],
        Tesla: ["ModelS", "Model3", "ModelX", "ModelY"],
        VW: ["Golf", "ID3", "ID4", "Tiguan", "Touareg", "Touran"],
    };
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
        "Windisch",
        "Winterthur",
        "Zürich",
    ];

    function getCarDetails(carId) {
        var config = {
            method: "get",
            url: api_root + "/api/car/" + carId, //Komplette URL für den Request erstellen, z.B: http://localhost:8080/api/car?pageSize=4&pageNumber=1
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                carDetails = response.data;
                mapUrl =
                    "https://maps.google.com/maps?width=1229&height=529&hl=en&q=%20" +
                    carDetails.carArea +
                    "+()&t=&z=11&ie=UTF8&iwloc=B&output=embed";
            })
            .catch(function (error) {
                alert("Konnte Fahrzeuginformationen nicht laden.");
                console.log(error);
            });
    }
    const carId = window.location.hash.split("/")[2]; // Füge diese Zeile hinzu, um die ID aus der URL zu extrahieren
    getCarDetails(carId); // Füge hier carId als Parameter hinzu

    function rentCar(carId) {
        isLoading = true;
        var config = {
            method: "put",
            url: api_root + "/api/service/me/rentcar?carId=" + carId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getCarDetails(carId);
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
                getCarDetails(carId);
            })
            .catch(function (error) {
                alert("Konnte Fahrzeug nicht entmieten.");
                console.log(error);
            })
            .finally(function () {
                isLoading = false;
            });
    }

    function updateCar(carId) {
        var config = {
            method: "put",
            url: api_root + "/api/car/" + carId,
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
            data: carDetails,
        };

        axios(config)
            .then(function (response) {
                alert("Änderungen gespeichert.");
                carDetails = response.data;
                showUpdateForm = false;
                getCarDetails(carId);
            })
            .catch(function (error) {
                alert("Konnte Fahrzeug nicht updaten.");
                console.log(error);
            });
    }

    function setCarAsAvailable(carId) {
        var config = {
            method: "put",
            url: api_root + "/api/car/" + carId + "/setAsAvailable",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
        };

        axios(config)
            .then(function (response) {
                alert("Fahrzeug wurde als Verfügbar markiert.");
                getCarDetails(carId);
            })
            .catch(function (error) {
                alert("Konnte Fahrzeug nicht updaten.");
                console.log(error);
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
            data: carDetails,
        };

        axios(config)
            .then(function (response) {
                alert("Fahrzeug gelöscht.");
                push('/cars');
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
                //"Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
        };

        axios(config)
            .then(function (response) {
                alert("Dein Vehicle wurde gelöscht.");
                push('/cars');
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
        <h2>Vehicle Informationen</h2>

        <div class="car-info-container">
            <img
                src={"images/" + carDetails.model + ".jpg"}
                alt={carDetails.model}
                class="car-image"
            />

            <div class="car-info">
                <h3>{carDetails.brand} {carDetails.model}</h3>
                <h6>Preis: {carDetails.price} CHF/Tag</h6>
                <p>
                    <img
                        src="images/design/calendar.png"
                        alt="calendar"
                        width="18"
                        style="margin-right: 7px; margin-top: -3px;"
                    />{carDetails.year}
                </p>
                <p>
                    <img
                        src="images/design/fuel.png"
                        alt="calendar"
                        width="18"
                        style="margin-right: 7px; margin-top: -3px;"
                    />{carDetails.carType}
                </p>
                <p>
                    <img
                        src="images/design/transmission.png"
                        alt="calendar"
                        width="18"
                        style="margin-right: 7px; margin-top: -3px;"
                    />{carDetails.carTransmission}
                </p>
                <p>
                    <img
                        src="images/design/pin.png"
                        alt="pin"
                        width="18"
                        style="margin-right: 7px; margin-top: -3px;"
                    />{carDetails.carArea}
                </p>
                <p>
                    <img
                        src="images/design/state.png"
                        alt="state"
                        width="18"
                        style="margin-right: 7px; margin-top: -3px;"
                    />{carDetails.carState}
                </p>
            </div>
        </div>
        <div class="car-description">
            <h3>Besitzer/in {carDetails.ownerName} sagt:</h3>
            <p>{carDetails.description}</p>

            <div class="button-container">
                {#if carDetails.userId === $myUserId}
                    <button
                        type="button"
                        class="btn btn-success btn-sm"
                        id="unrentButton"
                        on:click={() => {
                            unrentCar(carDetails.id);
                        }}>Miete beenden</button
                    >
                {:else if carDetails.ownerId === $myUserId}
                    <button
                        type="button"
                        class="btn btn-primary btn-sm"
                        id="updateButton"
                        on:click={() => {
                            showUpdateForm = true;
                        }}>Bearbeiten</button
                    >
                    <button
                        type="button"
                        class="btn btn-danger btn-sm"
                        id="deleteButton"
                        on:click={() => {
                            deleteMyCarById(carDetails.id);
                        }}>Löschen</button
                    >
                {:else if carDetails.userId === null && carDetails.ownerId !== $myUserId && !$actualUser.user_roles.includes("admin")}
                    <button
                        type="button"
                        class="btn btn-primary btn-sm"
                        id="rentButton"
                        on:click={() => {
                            rentCar(carDetails.id);
                        }}>Miete starten</button
                    >
                {/if}
                {#if $actualUser.user_roles && $actualUser.user_roles.includes("admin") && carDetails.carState === "Besetzt"}
                    <button
                        type="button"
                        class="btn btn-primary btn-sm"
                        id="setAvailableButton"
                        on:click={() => {
                            setCarAsAvailable(carDetails.id);
                        }}>Verfügbar setzen</button
                    >
                {/if}
                {#if $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
                    <button
                        type="button"
                        class="btn btn-danger btn-sm"
                        id="deleteButton"
                        on:click={() => {
                            deleteCar(carDetails.id);
                        }}>Löschen</button
                    >
                {/if}
            </div>
        </div>

        {#if showUpdateForm}
            <form on:submit|preventDefault={updateCar(carDetails.id)}>
                <div class="row mb-3">
                    <div class="col">
                        <label class="form-label" for="brand">Marke</label>
                        <select
                            bind:value={carDetails.brand}
                            class="form-select"
                            id="brand"
                        >
                            <option value="">Wähle eine Marke aus</option>
                            {#each brands as brand}
                                <option value={brand}>{brand}</option>
                            {/each}
                        </select>
                    </div>
                    <div class="col">
                        <label class="form-label" for="model">Modell</label>
                        <select
                            bind:value={carDetails.model}
                            class="form-select"
                            id="model"
                            disabled={!carDetails.brand}
                        >
                            <option value="">Wähle ein Modell</option>
                            {#if carDetails.brand}
                                {#each models[carDetails.brand] as model}
                                    <option value={model}>{model}</option>
                                {/each}
                            {/if}
                        </select>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col">
                        <label class="form-label" for="transmission"
                            >Getriebe</label
                        >
                        <select
                            bind:value={carDetails.carTransmission}
                            class="form-select"
                            id="transmission"
                            type="text"
                        >
                            <option value="Geschalten">Geschalten</option>
                            <option value="Automat">Automat</option>
                            <option value="Single">Single</option>
                        </select>
                    </div>
                    <div class="col">
                        <label class="form-label" for="cartype"
                            >Treibstoff</label
                        >
                        <select
                            bind:value={carDetails.carType}
                            class="form-select"
                            id="cartype"
                            type="text"
                        >
                            <option value="Elektrisch">Elektrisch</option>
                            <option value="Hybrid">Hybrid</option>
                            <option value="Diesel">Diesel</option>
                            <option value="Benzin">Benzin</option>
                        </select>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col">
                        <label class="form-label" for="year">Jahrgang</label>
                        <input
                            bind:value={carDetails.year}
                            class="form-control"
                            id="year"
                            type="number"
                        />
                    </div>
                    <div class="col">
                        <label class="form-label" for="carArea">Ort</label>
                        <select
                            bind:value={carDetails.carArea}
                            class="form-select"
                            id="carArea"
                        >
                            <option value="">Select an carArea</option>
                            {#each carAreas as carArea}
                                <option value={carArea}>{carArea}</option>
                            {/each}
                        </select>
                    </div>
                    <div class="col">
                        <label class="form-label" for="price"
                            >Preis in CHF/Tag</label
                        >
                        <input
                            bind:value={carDetails.price}
                            class="form-control"
                            id="price"
                            type="number"
                        />
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col">
                        <label class="form-label" for="description"
                            >Beschreibung</label
                        >
                        <textarea
                            bind:value={carDetails.description}
                            class="form-control"
                            id="description"
                            type="text"
                        />
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-sm" id="submitButton"
                    >Speichern</button
                >
                <button
                    type="button"
                    class="btn btn-danger btn-sm"
                    id="cancelButton"
                    on:click={() => {
                        showUpdateForm = false;
                    }}>Abbrechen</button
                >
            </form>
        {/if}

        <!-- svelte-ignore a11y-missing-attribute -->
        <iframe
            scrolling="no"
            marginheight="0"
            marginwidth="0"
            id="gmap_canvas"
            src={mapUrl}
            width="100%"
            height="575"
            frameborder="0"
        />
    {/if}
{:else}
    <p>Bitte melde Dich an.</p>
{/if}
