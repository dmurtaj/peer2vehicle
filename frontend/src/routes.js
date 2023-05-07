import Home from "./pages/Home.svelte";
import Users from "./pages/Users.svelte";
import Cars from "./pages/Cars.svelte";
import Account from "./pages/Account.svelte";

export default {
    '/': Home,
    '/home': Home,
    '/users': Users,
    '/cars': Cars,
    '/account': Account,
}