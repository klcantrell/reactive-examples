import { swapiData } from "~/stores/swapiStore";

export default function Header() {
  return <header>Status: {swapiData().status}</header>;
}
