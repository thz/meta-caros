PR := "${PR}.2"

RRECOMMENDS_${PN} = ""
RDEPENDS_${PN}_append = " kernel-module-nf-nat kernel-module-xt-addrtype "
