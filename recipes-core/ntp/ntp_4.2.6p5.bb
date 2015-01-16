SUMMARY = "Network Time Protocol daemon and utilities"
DESCRIPTION = "The Network Time Protocol (NTP) is used to \
synchronize the time of a computer client or server to \
another server or reference time source, such as a radio \
or satellite receiver or modem."
HOMEPAGE = "http://support.ntp.org"
SECTION = "console/network"
LICENSE = "NTP"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fea4b50c33b18c2194b4b1c9ca512670"

PR = "r3"

SRC_URI[md5sum] = "00df80a84ec9528fcfb09498075525bc"
SRC_URI[sha256sum] = "d6ab8371f9d31e594eb6922823d5ccd03dcc4e9d84b0e23ea25ac1405432f91c"

SRC_URI = "http://www.eecis.udel.edu/~ntp/ntp_spool/ntp4/ntp-4.2/ntp-${PV}.tar.gz \
           file://tickadj.c.patch \
           file://ntp-4.2.4_p6-nano.patch \
           file://openssl-check.patch \
           file://ntp.conf \
           file://ntpdate \
           file://ntpdate.default \
	   file://ntpdate.service \
	   file://ntpd.service \
           "

inherit autotools systemd

SYSTEMD_PACKAGES = "${PN} ntpdate"
SYSTEMD_SERVICE_${PN}= "ntpd.service"
SYSTEMD_SERVICE_ntpdate= "ntpdate.service"

# The ac_cv_header_readline_history is to stop ntpdc depending on either
# readline or curses
EXTRA_OECONF += "--with-net-snmp-config=no --without-ntpsnmpd ac_cv_header_readline_history_h=no"
CFLAGS_append = " -DPTYS_ARE_GETPT -DPTYS_ARE_SEARCHED"

PACKAGECONFIG ??= ""
PACKAGECONFIG[openssl] = "--with-openssl-libdir=${STAGING_LIBDIR} \
                          --with-openssl-incdir=${STAGING_INCDIR} \
                          --with-crypto, \
                          --without-openssl --without-crypto, \
                          openssl"

do_install_append() {
	install -d ${D}${sysconfdir}
	install -m 644 ${WORKDIR}/ntp.conf ${D}${sysconfdir}
	install -d ${D}${bindir}
	install -m 755 ${WORKDIR}/ntpdate ${D}${bindir}/ntpdate-sync

	# Fix hardcoded paths in scripts
	sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${bindir}/ntpdate-sync

	install -d ${D}/${sysconfdir}/default
	install -m 644 ${WORKDIR}/ntpdate.default ${D}${sysconfdir}/default/ntpdate
	install -d ${D}/${sysconfdir}/network/if-up.d
	ln -s ${bindir}/ntpdate-sync ${D}/${sysconfdir}/network/if-up.d
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/ntpdate.service ${D}${systemd_unitdir}/system/
	install -m 0644 ${WORKDIR}/ntpd.service ${D}${systemd_unitdir}/system/
}

PACKAGES += "ntpdate ${PN}-tickadj ${PN}-utils"
# NOTE: you don't need ntpdate, use "ntpd -q -g -x"

# ntp originally includes tickadj. It's split off for inclusion in small firmware images on platforms
# with wonky clocks (e.g. OpenSlug)
RDEPENDS_${PN} = "${PN}-tickadj"
# Handle move from bin to utils package
RPROVIDES_${PN}-utils = "${PN}-bin"
RREPLACES_${PN}-utils = "${PN}-bin"
RCONFLICTS_${PN}-utils = "${PN}-bin"

RSUGGESTS_${PN} = "iana-etc"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

FILES_${PN} = "${bindir}/ntpd ${sysconfdir}/ntp.conf ${sbindir} ${libdir}"
FILES_${PN}-tickadj = "${bindir}/tickadj"
FILES_${PN}-utils = "${bindir}"
FILES_ntpdate = "${bindir}/ntpdate ${sysconfdir}/network/if-up.d/ntpdate-sync ${bindir}/ntpdate-sync ${sysconfdir}/default/ntpdate"

CONFFILES_${PN} = "${sysconfdir}/ntp.conf"
CONFFILES_ntpdate = "${sysconfdir}/default/ntpdate"

pkg_postinst_ntpdate() {
if test "x$D" != "x"; then
        exit 1
else
        if ! grep -q -s ntpdate /var/spool/cron/root; then
                echo "adding crontab"
                test -d /var/spool/cron || mkdir -p /var/spool/cron
                echo "30 * * * *    ${bindir}/ntpdate-sync silent" >> /var/spool/cron/root
        fi
fi
}

