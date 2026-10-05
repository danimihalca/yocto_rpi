{pkgs ? import <nixpkgs> {}}: let
  fhs = pkgs.buildFHSEnvBubblewrap {
    name = "yocto-kas-fhs";
    targetPkgs = pkgs:
      with pkgs; [
        kas
        podman
      ];
  };
in
  fhs.env
