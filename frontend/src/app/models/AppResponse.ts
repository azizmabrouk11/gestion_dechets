import { UserProfile } from "./UserProfile";
import { Vehicule } from "./Vehicule";
import { Container } from "./Container";
import { PickUpPoint } from "./PickUpPoint";
import { Route } from "./Route";

export interface AppResponse {
    status: number,
    message: string,

    user?: UserProfile,
    users?: UserProfile[],

    vehicule?: Vehicule,
    vehicules?: Vehicule[],

    container?: Container,
    containers?: Container[],

    pickUpPoint?: PickUpPoint,
    pickUpPoints?: PickUpPoint[],

    route?: Route,
    routes?: Route[],

    time: string

}