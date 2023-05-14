<script>
    import axios from "axios";
    import { actualUser, jwt_token, myUserId, isAuthenticated } from "../store"; //Das JWT wird aus dem Store geladen

    const api_root = window.location.origin;

    let carDetails = [];
    let mapUrl = "";
    let showUpdateForm = false;

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
                alert("Could not get CarDetails");
                console.log(error);
            });
    }
    const carId = window.location.hash.split("/")[2]; // Füge diese Zeile hinzu, um die ID aus der URL zu extrahieren
    getCarDetails(carId); // Füge hier carId als Parameter hinzu

    function rentCar(carId) {
        var config = {
            method: "put",
            url: api_root + "/api/service/me/rentcar?carId=" + carId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getCarDetails();
            })
            .catch(function (error) {
                alert("Could not rent car");
                console.log(error);
            });
    }

    function unrentCar(carId) {
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
                alert("Could not unrent car");
                console.log(error);
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
                alert("Car updated");
                carDetails = response.data;
                showUpdateForm = false;
                getCarDetails(carId);
            })
            .catch(function (error) {
                alert("Could not update car");
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
                alert("Car deleted");
                getCars();
            })
            .catch(function (error) {
                alert("Could not delete Car");
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
                alert("Car deleted");
                getCars();
            })
            .catch(function (error) {
                alert("Could not delete Car");
                console.log(error);
            });
    }
</script>

{#if $isAuthenticated}

<h1>Car Details</h1>

<img
    src={"images/" + carDetails.model + ".jpg"}
    alt={carDetails.model}
    width="350"
/>

<div class="card" style="width: 18rem;">
    <div class="card-body">
        <h5 class="card-title">{carDetails.brand} {carDetails.model}</h5>
        <h6 class="card-subtitle mb-2 text-muted">Price: {carDetails.price}</h6>
        <p class="card-text">
            Year: {carDetails.year}
            Area: {carDetails.carArea}
            Type: {carDetails.carType}<br />
            Transmission: {carDetails.carTransmission}<br />
            State: {carDetails.carState}<br />
            Owner: {carDetails.ownerName}<br />
            Renter: {carDetails.userEmail}
        </p>
        <p class="card-text">{carDetails.description}</p>
        {#if carDetails.userId === $myUserId}
            <button
                type="button"
                class="btn btn-success btn-sm"
                on:click={() => {
                    unrentCar(carDetails.id);
                }}>Unrent Car</button
            >
        {:else if carDetails.ownerId === $myUserId}
            <span class="badge bg-secondary" id="myCar">My Car</span>
            <button
                type="button"
                class="btn btn-primary btn-sm"
                on:click={() => {
                    showUpdateForm = true;
                }}>Update Car</button
            >
            <button
                type="button"
                class="btn btn-danger btn-sm"
                id="deleteButton"
                on:click={() => {
                    deleteMyCarById(carDetails.id);
                }}>Delete</button
            >
        {:else if carDetails.userId === null && carDetails.ownerId !== $myUserId}
            <button
                type="button"
                class="btn btn-primary btn-sm"
                id="rentButton"
                on:click={() => {
                    rentCar(carDetails.id);
                }}>Rent Car</button
            >
        {:else}
            <span class="badge bg-secondary" id="rented">Unavailable</span>
        {/if}
        {#if $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
            <button
                type="button"
                class="btn btn-danger btn-sm"
                id="deleteButton"
                on:click={() => {
                    deleteCar(carDetails.id);
                }}>Delete</button
            >
        {/if}
    </div>
</div>

{#if showUpdateForm}
    <form on:submit|preventDefault={updateCar(carDetails.id)}>
        <label class="form-label" for="brand">Brand</label>
        <select bind:value={carDetails.brand} class="form-select" id="brand">
            <option value="">Select a brand</option>
            {#each brands as brand}
                <option value={brand}>{brand}</option>
            {/each}
        </select>

        <label class="form-label" for="model">Model</label>
        <select
            bind:value={carDetails.model}
            class="form-select"
            id="model"
            disabled={!carDetails.brand}
        >
            <option value="">Select a model</option>
            {#if carDetails.brand}
                {#each models[carDetails.brand] as model}
                    <option value={model}>{model}</option>
                {/each}
            {/if}
        </select>

        <label class="form-label" for="year">Year</label>
        <input
            bind:value={carDetails.year}
            class="form-control"
            id="year"
            type="number"
        />

        <label class="form-label" for="carArea">Area</label>
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

        <label class="form-label" for="price">Price</label>
        <input
            bind:value={carDetails.price}
            class="form-control"
            id="price"
            type="number"
        />

        <label class="form-label" for="cartype">Cartype</label>
        <select
            bind:value={carDetails.carType}
            class="form-select"
            id="cartype"
            type="text"
        >
            <option value="ELECTRIC">ELECTRIC</option>
            <option value="HYBRID">HYBRID</option>
            <option value="DIESEL">DIESEL</option>
            <option value="GAS">GAS</option>
        </select>

        <label class="form-label" for="transmission">Transmission</label>
        <select
            bind:value={carDetails.carTransmission}
            class="form-select"
            id="transmission"
            type="text"
        >
            <option value="MANUAL">MANUAL</option>
            <option value="AUTOMATIC">AUTOMATIC</option>
            <option value="SINGLE">SINGLE</option>
        </select>

        <label class="form-label" for="description">Description</label>
        <input
            bind:value={carDetails.description}
            class="form-control"
            id="description"
            type="text"
        />

        <button type="submit">Submit</button>
        <button
            type="button"
            on:click={() => {
                showUpdateForm = false;
            }}>Cancel</button
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
    width="1229"
    height="529"
    frameborder="0"
/>

{/if}