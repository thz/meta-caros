DESCRIPTION = "PostgreSQL is a powerful, open source relational database system."
HOMEPAGE = "http://www.postgresql.com"
LICENSE = "BSD"
DEPENDS = "zlib readline tzcode-native"
INC_PR = "r0"

ARM_INSTRUCTION_SET = "arm"

SRC_URI = "http://ftp.postgresql.org/pub/source/v${PV}/${P}.tar.bz2"

LEAD_SONAME = "libpq.so"

# LDFLAGS for shared libraries
export LDFLAGS_SL = "${LDFLAGS}"

inherit autotools pkgconfig useradd systemd

USERADD_PACKAGES = "${PN}"

# Add user and group for postgres
GROUPADD_PARAM_${PN} = "postgres"
USERADD_PARAM_${PN} = "--create-home \
            --system \
            --shell /bin/false \
            --gid postgres \
            --comment postgres \
            postgres"

SRC_URI += "file://${PN}.service"

EXTRA_OECONF = "--disable-rpath"
EXTRA_OECONF_sh4 = "--disable-spinlocks --disable-rpath"
EXTRA_OECONF_aarch64 += "--disable-spinlocks"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/postgresql.service ${D}/${systemd_unitdir}/system
}

SSTATE_SCAN_FILES += "Makefile.global"

PACKAGES =+ "${PN}-client ${PN}-server-dev ${PN}-timezone \
             libecpg-compat-dbg libecpg-compat \
             libecpg-dbg libecpg libecpg-dev \
             libpq-dbg libpq libpq-dev \ 
             libpgtypes-dbg libpgtypes "

FILES_${PN} += "${systemd_unitdir}/system/postgresql.service"

FILES_${PN}-client = "${bindir}/clusterdb \
                ${bindir}/createdb \
                ${bindir}/createlang \
                ${bindir}/createuser \
                ${bindir}/dropdb \
                ${bindir}/droplang \
                ${bindir}/dropuser \
                ${bindir}/pg_dump \
                ${bindir}/pg_dumpall \
                ${bindir}/pg_restore \
                ${bindir}/psql \
                ${bindir}/reindexdb \
                ${bindir}/vacuumdb \
                ${bindir}/vacuumlo \
                ${datadir}/${PN}/psqlrc.sample"

FILES_${PN}-doc += "${prefix}/doc/"
FILES_${PN}-timezone = "${datadir}/${PN}/timezone"
FILES_${PN}-server-dev = "${includedir}/${PN}/server"

FILES_libecpg = "${libdir}/libecpg*${SOLIBS}"
FILES_libecpg-dbg = "${libdir}/.debug/libecpg*"
FILES_libecpg-dev = "${libdir}/libecpg*${SOLIBSDEV} \
                     ${libdir}/libpgtypes*${SOLIBSDEV} \
                     ${includedir}/ecpg*.h ${includedir}/${PN}/ecpg*.h \
                     ${includedir}/pgtypes*.h ${includedir}/${PN}/informix \
                     ${includedir}/sql3types.h ${includedir}/sqlca.h"
FILES_libecpg-staticdev = "${libdir}/libecpg*.a"
SECTION_libecpg-staticdev = "devel"
RDEPENDS_libecpg-staticdev = "libecpg-dev (= ${EXTENDPKGV})"

FILES_libpq = "${libdir}/libpq*${SOLIBS}"
FILES_libpq-dbg = "${libdir}/.debug/libpq* ${libdir}/${PN}/pgxs/src/test/regress/.debug/*"
FILES_libpq-dev = "${libdir}/libpq*${SOLIBSDEV} \
                   ${includedir}"
FILES_libpq-staticdev = "${libdir}/libpq*.a"
SECTION_libpq-staticdev = "devel"
RDEPENDS_libpq-staticdev = "libpq-dev (= ${EXTENDPKGV})"

FILES_libecpg-compat = "${libdir}/libecpg_compat*${SOLIBS}"
FILES_libecpg-compat-dbg = "${libdir}/.debug/libecpg_compat*"
FILES_libpgtypes = "${libdir}/libpgtypes*${SOLIBS}"
FILES_libpgtypes-dbg = "${libdir}/.debug/libpgtypes*"
