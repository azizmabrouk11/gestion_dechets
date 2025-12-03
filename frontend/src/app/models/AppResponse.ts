import { UserProfile } from "./UserProfile";
import { Vehicule } from "./Vehicule";
import { Container } from "./Container";
import { PickUpPoint } from "./PickUpPoint";
import { Route } from "./Route";
import { Incident } from "./Incident";
import { Notification } from "./Notification";

export interface AppResponse {
    status: number,
    message: string,

    user?: UserProfile,
    users?: UserProfile[],

    vehicule?: Vehicule,
    vehicules?: Vehicule[],

    container?: Container,
    containers?: Container[],

    pickuppoint?: PickUpPoint,
    pickuppoints?: PickUpPoint[],

    route?: Route,
    routes?: Route[],

    incident?: Incident,
    incidents?: Incident[],

    notification?: Notification,
    notifications?: Notification[],

    time: string

}
