import Home from "./pages/Home.svelte";
import Mieter from "./pages/Mieter.svelte";
import Cars from "./pages/Cars.svelte";
import Account from "./pages/Account.svelte";

export default {
    '/': Home,
    '/home': Home,
    '/mieters': Mieter,
    '/cars': Cars,
    '/account': Account,
}