import { useLocation } from "react-router-dom";
import { buildDefaultClient, Jwts } from "../service/oauth2";
import { Client, OAuth2Client } from "../service/oauth2/OAuth2Client";

const Oauth2Redirect: React.FC<{}> = () => {
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);

    const state: string | null = queryParams.get("state");
    console.log(`state = ${state}`);
    if (state === null) {
        throw new Error("state is null");
    }
    const code: string | null = queryParams.get("code");
    console.log(`code = ${code}`);
    if (code === null) {
        throw new Error("code is null");
    }

    const oauth2Client: OAuth2Client = buildDefaultClient(Client.SPA);
    console.log();
    oauth2Client.fetchJwts(code, state, window.location.href.split("?")[0]);
    const jwts: Jwts | null = oauth2Client.getJwts();
    console.log(jwts);

    return <div>asd</div>;
};

export default Oauth2Redirect;
