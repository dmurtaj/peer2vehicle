import Home from "./pages/Home.svelte";
import Users from "./pages/Users.svelte";
import Cars from "./pages/Cars.svelte";
import CreateCars from "./pages/CreateCars.svelte";
import CarDetails from "./pages/CarDetails.svelte";
import Overview from "./pages/Overview.svelte";

export default {
    '/': Home,
    '/home': Home,
    '/users': Users,
    '/cars': Cars,
    '/createcars': CreateCars,
    '/car/:id': CarDetails,
    '/overview': Overview,
}