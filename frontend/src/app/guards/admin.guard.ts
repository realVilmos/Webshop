import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth-service/auth.service";

export const adminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const auth = inject(AuthService);

  if (auth.getAccessToken != null && auth.isAdmin()){
    return true
  }else{
    router.navigate(['/dashboard'])
    return false;
  }
};
