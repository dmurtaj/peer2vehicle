<script>
    import axios from "axios";
    import { jwt_token, myUserId, actualUser, isAuthenticated } from "../store"; //Das JWT wird aus dem Store geladen
    import { push } from "svelte-spa-router";

    const api_root = window.location.origin; //http://localhost:8080

    let car = {
        brand: null,
        model: null,
        year: null,
        carArea: null,
        price: null,
        carType: null,
        carTransmission: null,
        description: null,
        ownerName: null,
        ownerEmail: null,
        ownerId: null,
    };

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

    function createCar() {
        car.ownerId = $myUserId;
        car.ownerName = $actualUser.name;
        car.ownerEmail = $actualUser.email;
        var config = {
            method: "post",
            url: api_root + "/api/car",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
            data: car,
        };

        axios(config)
            .then(function (response) {
                alert("Vehicle erstellt!");
                //getCars();
                push("/cars");
            })
            .catch(function (error) {
                alert(
                    "Vehicle konnte nicht erstellt werden, überprüfe Deine Eingaben!"
                );
                console.log(error);
            });
    }
</script>

{#if $isAuthenticated}
    <h2>Vermiete dein Vehicle</h2>
    <form class="mb-5">
        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="brand">Marke</label>
                <select bind:value={car.brand} class="form-select" id="brand">
                    <option value="">Wähle eine Marke aus</option>
                    {#each brands as brand}
                        <option value={brand}>{brand}</option>
                    {/each}
                </select>
            </div>
            <div class="col">
                <label class="form-label" for="model">Modell</label>
                <select
                    bind:value={car.model}
                    class="form-select"
                    id="model"
                    disabled={!car.brand}
                >
                    <option value="">Wähle ein Modell</option>
                    {#if car.brand}
                        {#each models[car.brand] as model}
                            <option value={model}>{model}</option>
                        {/each}
                    {/if}
                </select>
            </div>
        </div>

        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="transmission">Getriebe</label>
                <select
                    bind:value={car.carTransmission}
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
                <label class="form-label" for="cartype">Treibstoff</label>
                <select
                    bind:value={car.carType}
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
                    bind:value={car.year}
                    class="form-control"
                    id="year"
                    type="number"
                />
            </div>
            <div class="col">
                <label class="form-label" for="carArea">Ort</label>
                <select
                    bind:value={car.carArea}
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
                <label class="form-label" for="price">Preis in CHF/Tag</label>
                <input
                    bind:value={car.price}
                    class="form-control"
                    id="price"
                    type="number"
                />
            </div>
        </div>

        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="description">Beschreibung</label>
                <textarea
                    bind:value={car.description}
                    class="form-control"
                    id="description"
                    type="text"
                />
            </div>
        </div>

        <button
            type="button"
            class="btn btn-primary"
            id="createButton"
            on:click={createCar}>Erstellen</button
        >
    </form>
{:else}
    <p>Bitte melde Dich an.</p>
{/if}
