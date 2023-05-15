<script>
    import axios from "axios";
    import { jwt_token, myUserId, actualUser, isAuthenticated } from "../store"; //Das JWT wird aus dem Store geladen
    import { push } from "svelte-spa-router"; //Wird benötigt, um Query-Parameter aus der aktuellen URL auszulesen, z.B.: http://localhost:8080/#/cars?pageNumber=2

    const api_root = window.location.origin;
    /*
    Hinweis: window.location.origin ist die Serveradresse der aktuellen Seiten. Beispiel: Wenn
    http://localhost:8080/#/cars angezeigt wird, ist window.location.origin gleich
    http://localhost:8080
    Dies hat den Vorteil, dass wir die URL später nicht anpassen müssen, wenn wir die Anwendung
    deployen.
    */

    //let currentPage;
    //let nrOfPages = 0;
    //let defaultPageSize = 4;
    /*In diesen Variablen merken wir uns, welche
    Page aktuell angezeigt wird und wie viele
    Pages es insgesamt gibt.*/

    //let priceMax;
    //let carState;
    //let carArea; //In den Input-Elementen eingetragene Werte

    //let cars = [];
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

    /*
    $: {
        let searchParams = new URLSearchParams($querystring);
        if (searchParams.has("page")) {
            currentPage = searchParams.get("page");
        } else {
            currentPage = "1";
        }
        //getCars();
    }*/
    /* Dieser Code wird immer dann ausgeführt, wenn sich der Wert einer Variablen im Code-Block $: {... } ändert,
    siehe auch https://svelte.dev/tutorial/reactive-statements
    Wir lesen hier den Query-Parameter "page" aus der URL und holen uns anschliessend alle Cars. */

    /*
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
        //Query-Parameter für den Request ans Backend ergänzen. Beispiel für eine komplette URL:
        //http://localhost:8080/api/car?pageSize=4&page=2&price=139&carType=TEST 

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
                alert("Could not get cars");
                console.log(error);
            });
    }
    */
    //getCars();
    /* getCars() wird neu im Reactive Statement weiter
    oben aufgerufen und kann hier gelöscht oder
    auskommentiert werden. */

    function createCar() {
        car.ownerId = $myUserId;
        car.ownerName = $actualUser.nickname;
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
                alert("Car created");
                //getCars();
                push('/cars');
            })
            .catch(function (error) {
                alert("Could not create Car");
                console.log(error);
            });
    }

    /*
    function rentCar(carId) {
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
    */
</script>

{#if $isAuthenticated}

<h1 class="mt-3">Create Car</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="brand">Brand</label>
            <select bind:value={car.brand} class="form-select" id="brand">
                <option value="">Select a brand</option>
                {#each brands as brand}
                    <option value={brand}>{brand}</option>
                {/each}
            </select>
        </div>
        <div class="col">
            <label class="form-label" for="model">Model</label>
            <select
                bind:value={car.model}
                class="form-select"
                id="model"
                disabled={!car.brand}
            >
                <option value="">Select a model</option>
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
            <label class="form-label" for="description">Description</label>
            <input
                bind:value={car.description}
                class="form-control"
                id="description"
                type="text"
            />
        </div>
    </div>
    <div class="col">
        <label class="form-label" for="year">Year</label>
        <input
            bind:value={car.year}
            class="form-control"
            id="year"
            type="number"
        />
    </div>
    <div class="col">
        <label class="form-label" for="carArea">Area</label>
        <select bind:value={car.carArea} class="form-select" id="carArea">
            <option value="">Select an carArea</option>
            {#each carAreas as carArea}
                <option value={carArea}>{carArea}</option>
            {/each}
        </select>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="cartype">Cartype</label>
            <select
                bind:value={car.carType}
                class="form-select"
                id="cartype"
                type="text"
            >
                <option value="ELECTRIC">ELECTRIC</option>
                <option value="HYBRID">HYBRID</option>
                <option value="DIESEL">DIESEL</option>
                <option value="GAS">GAS</option>
            </select>
        </div>
        <div class="col">
            <label class="form-label" for="transmission">Transmission</label>
            <select
                bind:value={car.carTransmission}
                class="form-select"
                id="transmission"
                type="text"
            >
                <option value="MANUAL">MANUAL</option>
                <option value="AUTOMATIC">AUTOMATIC</option>
                <option value="SINGLE">SINGLE</option>
            </select>
        </div>

        <div class="col">
            <label class="form-label" for="price">Price</label>
            <input
                bind:value={car.price}
                class="form-control"
                id="price"
                type="number"
            />
        </div>
    </div>
    <button
        type="button"
        class="btn btn-primary"
        id="submitbutton"
        on:click={createCar}>Submit</button
    >
</form>

{:else}
    <p>Bitte melde Dich an.</p>
{/if}