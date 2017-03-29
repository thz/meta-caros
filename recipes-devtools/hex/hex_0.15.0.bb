HOMEPAGE="https://hex.pm/"
SUMMARY="Hex is a package manager for the Erlang ecosystem."
LICENSE="GPLv2"

PR = "r1"

LIC_FILES_CHKSUM="file://README.md;beginline=29;md5=1675e23b7cf21ff6004f40e5abcfc75a"

SRC_URI = "https://github.com/hexpm/hex/archive/v${PV}.tar.gz;downloadfilename=${PN}-${PV}.tar.gz"
SRC_URI[md5sum] = "722f67196b69b1c25f65fb23d494cdd1"
SRC_URI[sha256sum] = "74c2958da91daa39c0e4593fcbe256802ed2741376dca206a9f75e1a3a854901"

DEPENDS += "elixir-native"

do_install() {
        install -d ${D}${libdir}/elixir/lib/
        MIX_ENV=release mix archive.build
        unzip -d ${D}${libdir}/elixir/lib/ hex-${PV}.ez
}

FILES_${PN} += "${libdir}/elixir/lib/hex-0.15.0"

BBCLASSEXTEND = "native"
